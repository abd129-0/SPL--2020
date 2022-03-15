package bgu.spl.net.srv;

import bgu.spl.net.impl.Pair;

import java.io.File;
import java.text.Collator;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Passive object representing the Database where all courses and users are stored.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can add private fields and methods to this class as you see fit.
 */
public class Database {
    private static Database singleton = new Database();
    private ConcurrentHashMap<String, String> adminAccounts;//done
    private ConcurrentHashMap<String, String> accounts;//done
    private ConcurrentHashMap<String, Boolean> logged;//done
    private ConcurrentHashMap<String, String[]> courses; //done!
    private ConcurrentHashMap<String, LinkedList<String>> userCourses;//
    private ConcurrentHashMap<String, LinkedList<String>> courseUsers;//
    private ConcurrentHashMap<String, Pair<Integer,Integer>> courseLimit; //done!
    private ConcurrentHashMap<String, String> courseDecode; //done!


    //to prevent user from creating new Database
    private Database() {
        // TODO: implement
        adminAccounts = new ConcurrentHashMap<>();
        accounts = new ConcurrentHashMap<>();
        logged = new ConcurrentHashMap<>();
        courses = new ConcurrentHashMap<>();
        userCourses = new ConcurrentHashMap<>();
        courseUsers = new ConcurrentHashMap<>();
        courseLimit = new ConcurrentHashMap<>();
        courseDecode = new ConcurrentHashMap<>();
        initialize("./Courses.txt");
    }

    /**
     * Retrieves the single instance of this class.
     */
    public static Database getInstance() {
        return singleton;
    }

    /**
     * loades the courses from the file path specified
     * into the Database, returns true if successful.
     */
    boolean initialize(String coursesFilePath) {
        // TODO: implement
        try {
            File file = new File(coursesFilePath);
            Scanner sc = new Scanner(file);
            sc.useDelimiter("\\n");
            while (sc.hasNext()) {
                String[] temp = sc.next().split("\\|");
                String[] kdam = temp[2].substring(1, temp[2].length() - 1).split(",");
                courses.putIfAbsent(temp[0], kdam);
                courseUsers.put(temp[0],new LinkedList<>());
                courseLimit.putIfAbsent(temp[0], new Pair<>(Integer.parseInt(temp[3].replace("\r", "")),Integer.parseInt(temp[3].replace("\r", ""))));
                courseDecode.putIfAbsent(temp[0], temp[1]);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    //1
    public boolean registerAdmin(String username, String password) {
        synchronized (logged) {
            synchronized (adminAccounts) {
                if (logged.containsKey(username))
                    return false;
                adminAccounts.put(username, password);
                logged.put(username, false);
                return true;
            }
        }
    }
    //2
    public boolean register(String username, String password) {
        synchronized (logged) {
            synchronized (accounts) {
                if (logged.containsKey(username))
                    return false;
                accounts.put(username, password);
                logged.put(username, false);
                userCourses.put(username, new LinkedList<>());
                return true;
            }
        }
    }
//3
    public boolean login(String username, String password) {
                    if (!logged.containsKey(username) || logged.get(username) ||
                            (accounts.containsKey(username) && !accounts.get(username).equals(password)) ||
                            (adminAccounts.containsKey(username) && !adminAccounts.get(username).equals(password)))
                        return false;
            
            logged.put(username, true);
            return true;
    }
//4
    public boolean logout(String username) {
            if (!logged.containsKey(username) || !logged.get(username))
                return false;
            logged.put(username, false);
            return true;
    }
    //5
    public boolean registerCourse(String username, String course) {
                if (!accounts.containsKey(username) || !logged.get(username) ||
                        !courses.containsKey(course) || isRegistered(username, course) ||
                        !checkKdam(username, course))
                    return false;
            
            int availableSeats = courseLimit.get(course).getFirst();
            if (availableSeats == 0)
                return false;
            userCourses.get(username).add(course);
            courseUsers.get(course).add(username);
            courseLimit.get(course).setFirst(availableSeats - 1);
            return true;
    }
    public boolean checkKdam(String username, String course) {
        synchronized (courses) {
            String[] kdamCourses = courses.get(course);
            if (kdamCourses[0].equals(""))
                return true;
            LinkedList<String> coursesList = userCourses.get(username);
            for (int i = 0; i < kdamCourses.length; i++) {
                if (!isRegistered(username, kdamCourses[i]))
                    return false;
            }
            return true;
        }
    }
    //6
    public String kdamCheck(String course) {
            return Arrays.deepToString(courses.get(course)).replaceAll(", ", ",");
    }
    //7
    public String courseStat(String course) {
        synchronized (courses) {
            synchronized (courseUsers) {
                if (courses.containsKey(course)) {
                    if (courseUsers.get(course) != null) {
                        LinkedList<String> users = courseUsers.get(course);
                        users.sort(String::compareToIgnoreCase);
                        return "Course: (" + course + ") " + courseDecode.get(course) +
                                "\nSeats Available: " + courseLimit.get(course) +
                                "\nStudents Registered: " + users.toString().replaceAll(", ", ",");
                    }
                }
            }
            return null;
        }
    }
    //8
    public String studentStat(String username) {
        if (accounts.containsKey(username)) {
            if(userCourses.get(username) == null){
                return "Student: " + username +
                        "\nCourses: []";
            } else {
                return "Student: " + username +
                        "\nCourses: " + userCourses.get(username).toString().replaceAll(", ", ",");
            }
        }
        return null;
    }
//9
    public boolean isRegistered(String username, String course) {
            if (userCourses.get(username) != null)
                for (String str : userCourses.get(username))
                    if (str.equals(course))
                        return true;
            return false;
    }
    //10
    public boolean unregisterCourse(String username, String course) {
        if (!accounts.containsKey(username) || !logged.get(username))
            return false;
        int availableSeats = courseLimit.get(course).getFirst();
        courseLimit.get(course).setFirst(availableSeats+1);
        courseUsers.get(course).remove(username);
        return userCourses.get(username).remove(course);
    }
    //11
    public String myCourses(String username){
        if(!accounts.containsKey(username))
            return null;
        LinkedList<String> temp = userCourses.get(username);
        if(temp == null || temp.size() == 0)
            return "[]";
        String ans = "[";
        for(String str: temp){
            ans = ans + str + ",";
        }
        return ans.substring(0,ans.length()-1) + "]";
    }

    public boolean isAdmin(String username){
        return adminAccounts.containsKey(username);
    }
}
