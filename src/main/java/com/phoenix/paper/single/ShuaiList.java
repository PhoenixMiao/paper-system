package com.phoenix.paper.single;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ShuaiList extends ShuaiObject {

    private CopyOnWriteArrayList<ShuaiObject> list;

    public ShuaiList() {
        this.list = new CopyOnWriteArrayList<>();
        this.objectType = ShuaiObjectType.SHUAI_LIST;
    }

    public static ShuaiReply lPush(String[] argv, ShuaiDB db) {
        ShuaiList shuaiList;
        try {
            if (db.getDict().containsKey(new ShuaiString(argv[1])))
                shuaiList = (ShuaiList) db.getDict().get(new ShuaiString(argv[1]));
            else {
                shuaiList = new ShuaiList();
                db.getDict().put(new ShuaiString(argv[1]), shuaiList);
            }
            List<ShuaiObject> newValueList = new ArrayList<>();
            String[] newValue = argv[2].split(" ");
            for (String value : newValue) {
                newValueList.add(new ShuaiString(value));
            }
            shuaiList.list.addAll(0, newValueList);
            return new ShuaiReply(ShuaiReplyStatus.OK, new ShuaiString(shuaiList.list.size() + ""));
        } catch (ClassCastException e) {
            return new ShuaiReply(ShuaiReplyStatus.INNER_FAULT, ShuaiErrorCode.TYPE_FORMAT_FAULT);
        }
    }

    public static ShuaiReply rPush(String[] argv, ShuaiDB db) {
        ShuaiList shuaiList;
        try {
            if (db.getDict().containsKey(new ShuaiString(argv[1])))
                shuaiList = (ShuaiList) db.getDict().get(new ShuaiString(argv[1]));
            else {
                shuaiList = new ShuaiList();
                db.getDict().put(new ShuaiString(argv[1]), shuaiList);
            }
            List<ShuaiObject> newValueList = new ArrayList<>();
            String[] newValue = argv[2].split(" ");
            for (String value : newValue) {
                newValueList.add(new ShuaiString(value));
            }
            shuaiList.list.addAll(newValueList);
            return new ShuaiReply(ShuaiReplyStatus.OK, new ShuaiString(shuaiList.list.size() + ""));
        } catch (ClassCastException e) {
            return new ShuaiReply(ShuaiReplyStatus.INNER_FAULT, ShuaiErrorCode.TYPE_FORMAT_FAULT);
        }
    }

    public CopyOnWriteArrayList<ShuaiObject> getList() {
        return list;
    }

    public ShuaiReply lRange(String[] argv, ShuaiDB db) {
        try {
            int begin = Integer.parseInt(argv[2]);
            int end = Integer.parseInt(argv[3]);
            Object[] temp = this.list.toArray();
            if (temp.length == 0) {
                return new ShuaiReply(ShuaiReplyStatus.OK, new ShuaiString("Empty list"));
            } else {
                if (begin < 0) {
                    begin += temp.length;
                }
                if (end < 0) {
                    end += temp.length;
                }
                StringBuilder res = new StringBuilder();
                for (int i = begin; i <= end; ++i) {
                    ShuaiString elem = (ShuaiString) temp[i];
                    res.append(i).append(") ").append(elem.getValue().toString()).append("\n");
                }
                return new ShuaiReply(ShuaiReplyStatus.OK, new ShuaiString(res.toString()));
            }
        } catch (NumberFormatException e) {
            return new ShuaiReply(ShuaiReplyStatus.INPUT_FAULT, ShuaiErrorCode.TYPE_FORMAT_FAULT);
        } catch (IndexOutOfBoundsException e) {
            return new ShuaiReply(ShuaiReplyStatus.INNER_FAULT, ShuaiErrorCode.OUT_OF_RANGE_FAULT);
        }
    }

    public ShuaiReply lLen(String[] argv, ShuaiDB db) {
        return new ShuaiReply(ShuaiReplyStatus.OK, new ShuaiString(this.list.size() + ""));
    }

    public ShuaiReply lPop(String[] argv, ShuaiDB db) {
        try {
            list.remove(0);
            return new ShuaiReply(ShuaiReplyStatus.OK, new ShuaiString(list.size() + ""));
        } catch (ClassCastException e) {
            return new ShuaiReply(ShuaiReplyStatus.INNER_FAULT, ShuaiErrorCode.TYPE_FORMAT_FAULT);
        } catch (IndexOutOfBoundsException e) {
            return new ShuaiReply(ShuaiReplyStatus.INNER_FAULT, ShuaiErrorCode.OUT_OF_RANGE_FAULT);
        }
    }

    public ShuaiReply rPop(String[] argv, ShuaiDB db) {
        try {
            list.remove(list.size() - 1);
            return new ShuaiReply(ShuaiReplyStatus.OK, new ShuaiString(list.size() + ""));
        } catch (ClassCastException e) {
            return new ShuaiReply(ShuaiReplyStatus.INNER_FAULT, ShuaiErrorCode.TYPE_FORMAT_FAULT);
        } catch (IndexOutOfBoundsException e) {
            return new ShuaiReply(ShuaiReplyStatus.INNER_FAULT, ShuaiErrorCode.OUT_OF_RANGE_FAULT);
        }
    }

    public ShuaiReply lIndex(String[] argv, ShuaiDB db) {
        try {
            int index = Integer.parseInt(argv[2]);
            if (index < 0) {
                index += list.size();
            }
            ShuaiString res = (ShuaiString) list.get(index);
            return new ShuaiReply(ShuaiReplyStatus.OK, res);
        } catch (NumberFormatException e) {
            return new ShuaiReply(ShuaiReplyStatus.INPUT_FAULT, ShuaiErrorCode.TYPE_FORMAT_FAULT);
        } catch (IndexOutOfBoundsException e) {
            return new ShuaiReply(ShuaiReplyStatus.INNER_FAULT, ShuaiErrorCode.OUT_OF_RANGE_FAULT);
        }
    }

    public ShuaiReply lInsert(String[] argv, ShuaiDB db) {
        try {
            String position = argv[2];
            ShuaiString pivot = new ShuaiString(argv[3]);
            ShuaiString value = new ShuaiString(argv[4]);
            int index = list.indexOf(pivot);
            if ("BEFORE".equalsIgnoreCase(position)) {
                list.add(index, value);
            } else if ("AFTER".equalsIgnoreCase(position)) {
                list.add(index + 1, value);
            } else {
                return new ShuaiReply(ShuaiReplyStatus.INPUT_FAULT, ShuaiErrorCode.ARGUMENT_WRONG);
            }
            return new ShuaiReply(ShuaiReplyStatus.OK, new ShuaiString(list.size() + ""));
        } catch (ClassCastException e) {
            return new ShuaiReply(ShuaiReplyStatus.INNER_FAULT, ShuaiErrorCode.TYPE_FORMAT_FAULT);
        } catch (IndexOutOfBoundsException e) {
            return new ShuaiReply(ShuaiReplyStatus.INNER_FAULT, ShuaiErrorCode.OUT_OF_RANGE_FAULT);
        }
    }

    public ShuaiReply lRem(String[] argv, ShuaiDB db) {
        try {
            int count = Integer.parseInt(argv[2]);
            ShuaiString value = new ShuaiString(argv[3]);
            if (count == 0) {
                list.removeIf(v -> v.equals(value));
            } else if (count > 0) {
                int c = 0;
                int index = list.indexOf(value);
                while (c < count && index != -1) {
                    c++;
                    list.remove(index);
                    index = list.indexOf(value);
                }
            } else {
                count = -count;
                int c = 0;
                int index = list.lastIndexOf(value);
                while (c < count && index != -1) {
                    c++;
                    list.remove(index);
                    index = list.lastIndexOf(value);
                }
            }
            return new ShuaiReply(ShuaiReplyStatus.OK, new ShuaiString(list.size() + ""));
        } catch (NumberFormatException e) {
            return new ShuaiReply(ShuaiReplyStatus.INPUT_FAULT, ShuaiErrorCode.TYPE_FORMAT_FAULT);
        }
    }

    public ShuaiReply lTrim(String[] argv, ShuaiDB db) {
        try {
            int begin = Integer.parseInt(argv[2]);
            int end = Integer.parseInt(argv[3]);
            if (begin < 0) {
                begin += list.size();
            }
            if (end < 0) {
                end += list.size();
            }
            ArrayList<ShuaiObject> newList = new ArrayList<>();
            for (int i = begin; i <= end; i++) {
                newList.add(list.get(i));
            }
            list.clear();
            list.addAll(newList);
            return new ShuaiReply(ShuaiReplyStatus.OK, new ShuaiString("OK"));
        } catch (NumberFormatException e) {
            return new ShuaiReply(ShuaiReplyStatus.INPUT_FAULT, ShuaiErrorCode.TYPE_FORMAT_FAULT);
        } catch (IndexOutOfBoundsException e) {
            return new ShuaiReply(ShuaiReplyStatus.INNER_FAULT, ShuaiErrorCode.OUT_OF_RANGE_FAULT);
        }
    }

    public ShuaiReply lSet(String[] argv, ShuaiDB db) {
        try {
            int index = Integer.parseInt(argv[2]);
            if (index < 0) {
                index += list.size();
            }
            ShuaiString value = new ShuaiString(argv[3]);
            list.set(index, value);
            return new ShuaiReply(ShuaiReplyStatus.OK, new ShuaiString("OK"));
        } catch (NumberFormatException e) {
            return new ShuaiReply(ShuaiReplyStatus.INPUT_FAULT, ShuaiErrorCode.TYPE_FORMAT_FAULT);
        } catch (IndexOutOfBoundsException e) {
            return new ShuaiReply(ShuaiReplyStatus.INNER_FAULT, ShuaiErrorCode.OUT_OF_RANGE_FAULT);
        }
    }

    @Override
    public String toString() {
        StringBuilder res = new StringBuilder();
        for (int i = 0; i < list.size(); ++i) {
            ShuaiString elem = (ShuaiString) list.get(i);
            res.append(i).append(") ").append(elem.getValue().toString()).append("\n");
        }
        return res.toString();
    }
}
