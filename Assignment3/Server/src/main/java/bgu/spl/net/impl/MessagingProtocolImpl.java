package bgu.spl.net.impl;

import bgu.spl.net.api.MessagingProtocol;
import bgu.spl.net.srv.Database;

import java.time.LocalDateTime;

public class MessagingProtocolImpl<T> implements MessagingProtocol {
    private boolean terminate = false;
    private Database database = Database.getInstance();
    private String username = "";
    private boolean isAdmin = false;

    public MessagingProtocolImpl(){}
    @Override
    public Object process(Object msg) {
        if(msg.equals(""))
            return null;
        switch (getOpcode(msg.toString())) {
            case 1:
                if (username.equals("") && database.registerAdmin(getFirstArg(msg.toString()), getSecondArg(msg.toString()))) return "12 1";
                else return "13 1";
            case 2:
                if (username.equals("") && database.register(getFirstArg(msg.toString()), getSecondArg(msg.toString()))) return "12 2";
                else return "13 2";
            case 3:
                if (username.equals("") && database.login(getFirstArg(msg.toString()), getSecondArg(msg.toString()))) {
                    username = getFirstArg(msg.toString());
                    if(database.isAdmin(username))
                        isAdmin = true;
                    return "12 3";//not sure if we need an ack
                } else return "13 3";
            case 4:
                if (database.logout(username)) {
                    terminate = true;
                    username = "";
                    isAdmin = false;
                    return "12 4";
                }
                else return "13 4";
            case 5:
                if ((!username.equals("") & !isAdmin) && database.registerCourse(username, getFirstArg(msg.toString()))) return "12 5";
                return "13 5";
            case 6:
                if(!username.equals("") & !isAdmin) return "12 6 " + database.kdamCheck(getFirstArg(msg.toString()));
                return "13 6";
            case 7:
                if(isAdmin & !username.equals("")) {
                    String result = database.courseStat(getFirstArg(msg.toString()));
                    if(result != null)
                        return "12 7 " + result;
                }
                return "13 7";
            case 8:
                if(isAdmin & !username.equals("")) {
                    String result = database.studentStat(getFirstArg(msg.toString()));
                    if(result != null)
                        return "12 8 " + result;
                }
                return "13 8";
            case 9:
                if (!isAdmin & !username.equals(""))
                    if (database.isRegistered(username, getFirstArg(msg.toString()))) return "12 9 REGISTERED";
                    else return "12 9 NOT REGISTERED";
                else return "13 9";
            case 10:
                if ((!isAdmin & !username.equals("")) && database.unregisterCourse(username, getFirstArg(msg.toString()))) return "12 10";
                else return "13 10";
            case 11:
                if(!isAdmin & !username.equals("")) return "12 11 " + database.myCourses(username);
                else return "13 11";
        }
        //terminate = "4".equals(msg);
        //process the message
        return null;
    }

    @Override
    public boolean shouldTerminate() {
        return terminate;
    }

    private int getOpcode(String msg) {
        String[] temp = msg.split(" ");
        return Integer.parseInt(temp[0]);
    }

    private String getFirstArg(String msg) {
        String[] temp = msg.split(" ");
        return temp[1];
    }

    private String getSecondArg(String msg) {
        String[] temp = msg.split(" ");
        return temp[2];
    }
}
