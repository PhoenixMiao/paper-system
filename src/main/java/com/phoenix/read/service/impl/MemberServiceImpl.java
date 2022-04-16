package com.phoenix.read.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.phoenix.read.common.CommonErrorCode;
import com.phoenix.read.common.CommonException;
import com.phoenix.read.common.Page;
import com.phoenix.read.common.PageParam;
import com.phoenix.read.dto.Order;
import com.phoenix.read.entity.Activity;
import com.phoenix.read.entity.Member;
import com.phoenix.read.entity.User;
import com.phoenix.read.mapper.ActivityMapper;
import com.phoenix.read.mapper.MemberMapper;
import com.phoenix.read.mapper.OrganizerMapper;
import com.phoenix.read.mapper.UserMapper;
import com.phoenix.read.service.MemberService;
import com.phoenix.read.util.SpringContextUtil;
import com.phoenix.read.util.TimeUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class MemberServiceImpl implements MemberService {

    @Autowired
    private MemberMapper memberMapper;

    @Autowired
    private ActivityMapper activityMapper;

    @Autowired
    private OrganizerMapper organizerMapper;

    @Autowired
    private UserMapper userMapper;

    @Override
    public Long makeOrder(Long activityId, Long userId) {
        Activity activity = activityMapper.selectByPrimaryKey(activityId);
        if(activity.getStatus()==-1) throw new CommonException(CommonErrorCode.ACTIVITY_HAS_END);
        User user = userMapper.selectByPrimaryKey(userId);
        if(user.getDepartment()==null||user.getMajor()==null||user.getName()==null||user.getStudentId()==null||user.getGrade()==null) throw new CommonException(CommonErrorCode.SELF_INFORMATION_UNWRITTEN);
        if(memberMapper.getMemberByUserIdAndActivityId(userId,activity.getId())!=null) throw  new CommonException(CommonErrorCode.ORDER_HAS_MADE);
        //todo 乐观锁
        activityMapper.makeOrder(activity.getPeople()+1,activityId);
        return memberMapper.makeOrder(activityId,userId, TimeUtil.getCurrentTimestamp(),0);
    }

    @Override
    public void check(Long id) {
        Member member = memberMapper.selectByPrimaryKey(id);
        Activity activity = activityMapper.selectByPrimaryKey(member.getActivityId());
        if(activity.getType()==-1) throw new CommonException(CommonErrorCode.ACTIVITY_HAS_END);
        memberMapper.updateStatus(1,id);
    }

    @Override
    public Page<Order> getMemberListByUserId(int pageSize, int pageNum, Long userId) {
        PageHelper.startPage(pageNum,pageSize);
        List<Member> memberList = memberMapper.getMemberListByUserId(userId);
        Page page = new Page(new PageInfo<>(memberList));
        ArrayList<Order> orderArrayList = new ArrayList<>();
        for(Member ele:memberList){
            Activity activity = activityMapper.selectByPrimaryKey(ele.getActivityId());
            orderArrayList.add(new Order(ele.getId(),activity.getName(),organizerMapper.selectByPrimaryKey(activity.getOrganizerId()).getName(),userMapper.selectByPrimaryKey(activity.getPublisherId()).getName(),activity.getStartTime(),activity.getStatus()));
        }
        orderArrayList.sort(new Comparator<Order>(){
            @Override
            public int compare(Order a,Order b) {
                return a.getStartTime().compareTo(b.getStartTime()); //这是顺序
            }
        });
        return new Page(new PageParam(pageSize,pageNum,"startTime desc"),page.getTotal(),page.getPages(),orderArrayList);
    }

    @Override
    public Member getMemberById(Long id) {
        return memberMapper.selectByPrimaryKey(id);
    }
}

@NoArgsConstructor
@AllArgsConstructor
@Builder
class MemberThead {
    private String timeStamp;
    private int type;
    private Long id;

    public void updateStatus() {
        try {
            SimpleDateFormat mDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date parse = mDateFormat.parse(timeStamp);
            Calendar calendar = Calendar.getInstance();
            if(type==2) calendar.add(Calendar.DAY_OF_MONTH,3);
            else calendar.setTime(parse);
            Date time = calendar.getTime();
            Timer timer = new Timer();
            if(type==0) timer.schedule(new OrderStart(id), time);
            else if(type==1) timer.schedule(new ActivityStart(id),time);
            else if(type==-1) timer.schedule(new ActivityEnd(id),time);
            else if(type==2) timer.schedule(new Mute(id),time);
        }catch (ParseException e){
            e.printStackTrace();
        }
    }
}


class OrderStart extends TimerTask {

    private ActivityMapper activityMapper;

    private Long id;

    public OrderStart(Long id){
        super();
        activityMapper = SpringContextUtil.getBean("ActivityMapper");
        this.id = id;
    }

    public OrderStart(){
        super();
    }


    @Override
    public void run() {
        try {
            if (activityMapper == null) {  //这个判断是用老方法@Autowired注入的时候 报空指针 测试的时候在这儿判断了一下 是因为service空 没有成功注入 所有service/dao注入需要SpringContextUtil.getBean才可以
                System.out.println("---> null");
            }
            this.activityMapper.updateStatus(0,this.id);//这里 调用service的业务逻辑

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

class ActivityStart extends TimerTask {
    private ActivityMapper activityMapper;

    private Long id;

    public ActivityStart(Long id){
        super();
        activityMapper = SpringContextUtil.getBean("ActivityMapper");
        this.id = id;
    }

    public ActivityStart(){
        super();
    }


    @Override
    public void run() {
        try {
            if (activityMapper == null) {  //这个判断是用老方法@Autowired注入的时候 报空指针 测试的时候在这儿判断了一下 是因为service空 没有成功注入 所有service/dao注入需要SpringContextUtil.getBean才可以
                System.out.println("---> null");
            }
            this.activityMapper.updateStatus(1,this.id);//这里 调用service的业务逻辑

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

class ActivityEnd extends TimerTask {
    private ActivityMapper activityMapper;
    private MemberMapper memberMapper;

    private Long id;

    public ActivityEnd(Long id){
        super();
        activityMapper = SpringContextUtil.getBean("ActivityMapper");
        memberMapper = SpringContextUtil.getBean("MemberMapper");
        this.id = id;
    }

    public ActivityEnd(){
        super();
    }

    @Override
    public void run() {
        try {
            if (this.activityMapper == null) {  //这个判断是用老方法@Autowired注入的时候 报空指针 测试的时候在这儿判断了一下 是因为service空 没有成功注入 所有service/dao注入需要SpringContextUtil.getBean才可以
                System.out.println("---> null");
            }
            this.activityMapper.updateStatus(-1,this.id);
            if(this.activityMapper.selectByPrimaryKey(this.id).getIsCheck()==1){
                List<Member> memberList = this.memberMapper.getUncommittedMemberList(this.id,0);
                for(Member ele:memberList){
                    this.memberMapper.updateStatus(-1,ele.getId());
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

class Mute extends TimerTask {
    private UserMapper userMapper;

    private Long id;

    public Mute(Long id){
        super();
        userMapper = SpringContextUtil.getBean("UserMapper");
        this.id = id;
    }

    public Mute(){
        super();
    }


    @Override
    public void run() {
        try {
            if (userMapper == null) {  //这个判断是用老方法@Autowired注入的时候 报空指针 测试的时候在这儿判断了一下 是因为service空 没有成功注入 所有service/dao注入需要SpringContextUtil.getBean才可以
                System.out.println("---> null");
            }
            this.userMapper.muteUser(0,this.id);//这里 调用service的业务逻辑

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
