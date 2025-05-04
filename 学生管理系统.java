import java.util.Scanner;
import java.util.Vector;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

// 学生管理系统主类
public class 学生管理系统 {
    public static void main(String[] args){
        // 从文件加载学生数据
        Vector<Student> stu = Student.loadStudentsFromFile("students.txt");
        Scanner sc = new Scanner(System.in);
        
        // 主循环，持续接收用户输入
        while(true){
            // 显示菜单选项
            System.out.println("输入选项：\n0.退出\n1.添加学生\n2.删除学生\n3.查询学生\n4.显示成绩单\n5.按成绩排序\n6.生成测试数据");
            int n = sc.nextInt();
            
            // 处理用户选择
            if(n == 0){
                // 退出程序
                System.out.println("感谢使用！");
                break;
            }
            else if(n == 1){
                // 添加学生信息
                System.out.println("请输入学生学号：(七位数字)");
                String id_number = sc.next();
                // 验证学号格式
                if(id_number.length() != 7) {System.out.println("无效输入, 请重试"); continue;}
                for(int i = 0; i < id_number.length(); i++){
                    if(id_number.charAt(i) <'0' || id_number.charAt(i) > '9'){
                        System.out.println("无效输入, 请重试");
                        continue;
                    }
                }
                System.out.println("请输入学生姓名和性别（男/女）：");
                String name = sc.next(), gender = sc.next();
                
                // 验证性别输入
                while(!gender.equals("男") && !gender.equals("女")) {
                    System.out.println("性别输入错误，请重新输入(男/女)");
                    gender = sc.next();
                }
                
                // 获取并验证年龄
                int age = 0;
                boolean validAge = false;
                while(!validAge) {
                    try {
                        System.out.println("请输入学生年龄：");
                        age = sc.nextInt();
                        validAge = true;
                    } catch (java.util.InputMismatchException e) {
                        System.out.println("年龄必须是整数，请重新输入！");
                        sc.next(); 
                    }
                }
                
                // 获取并验证成绩
                System.out.println("请输入学生成绩(0-5分): ");
                double score = sc.nextDouble();
                while(score < 0 || score > 5) {
                    System.out.println("分数格式错误，请重新输入(0-5)");
                    score = sc.nextDouble();
                }
                
                // 创建新学生对象并保存
                Student newStudent = new Student(id_number, name, gender, age, score);
                Vector<Student> existing = Student.loadStudentsFromFile("students.txt");
                existing.add(newStudent);
                Student.saveStudentsToFile(existing);
                System.out.println("操作成功！");
            }
            else if(n == 2){
                if(stu != null){
                    Vector<Student> savedStudents = Student.loadStudentsFromFile("students.txt");
                    Student.showTable(savedStudents);
                    System.out.println("请输入要删除的学生学号：");
                    String id_number = sc.next();
                    boolean isFound = false;
                    for(int i = 0; i < savedStudents.size(); i++){
                        if(id_number.equals(savedStudents.get(i).getID())){
                            isFound = true;
                            savedStudents.remove(i);
                        }
                    }
                    if(!isFound) System.out.println("无效输入，请重新尝试！");
                    else {
                        Student.saveStudentsToFile(savedStudents);
                        stu = savedStudents;
                        System.out.println("操作成功！");
                    }
                }
            }
            else if(n == 3){
                Vector<Student> savedStudents = Student.loadStudentsFromFile("students.txt");
                Student.showTable(savedStudents);
                System.out.println("请输入要查询的学生学号：");
                String id_number = sc.next();
                for(int i = 0; i < stu.size(); i++){
                    if(id_number.equals(stu.get(i).getID())){
                        System.out.println("学生信息：" + stu.get(i).toString());
                        break;
                    }
                }
            }
            else if(n == 4){
                Vector<Student> savedStudents = Student.loadStudentsFromFile("students.txt");
                Student.sorted_by_score(savedStudents);
                Student.showTable(savedStudents);  
            }
            else if(n == 5){
                Vector<Student> savedStudents = Student.loadStudentsFromFile("students.txt");
                Student.sorted_by_score(savedStudents);
                Student.showTable(savedStudents);
                Student.saveStudentsToFile(savedStudents);
                System.out.println("已按成绩排序并保存！");
            }
            else if(n == 6){
                System.out.println("警告：这将覆盖现有数据！输入'confirm'继续：否则输入任意字符退出");
                if(sc.next().equals("confirm")){
                    Vector<Student> testData = Student.generateSampleData(100);
                    Student.saveStudentsToFile(testData);
                    System.out.println("已生成100条测试数据！");
                }
                else{
                    System.out.println("已退出");
                }
            }
            else{
                System.out.println("无效输入, 请重试");
            }
        }
    }
}

// 学生类，用于存储学生信息
class Student{
    // 学生属性
    private String id, name, gender;
    private int age;
    private double score;
    
    // 构造函数
    Student(String id, String name, String gender, int age, double score){
        this.id = id; this.name = name; this.gender = gender;
        this.age = age; this.score = score;
    }
    
    // 按成绩排序方法
    public static void sorted_by_score(Vector<Student> stu){
        // 使用冒泡排序算法
        for(int i = 0; i < stu.size() - 1; i++){
            for(int j = 0; j < stu.size() - i - 1; j++){
                if(stu.get(j).getScore() < stu.get(j+1).getScore()){
                    // 交换位置
                    Student temp = stu.get(j);
                    stu.set(j, stu.get(j+1));
                    stu.set(j+1, temp);
                }
            }
        }
    }
    
    // 保存学生数据到文件
    public static void saveStudentsToFile(Vector<Student> stu){
        // 先排序再保存
        Student.sorted_by_score(stu);
        try (java.io.OutputStreamWriter writer = new java.io.OutputStreamWriter(
                new java.io.FileOutputStream("students.txt"), StandardCharsets.UTF_8)) {
            for (Student s : stu) {
                writer.write(s.getID() + "," + s.getName() + "," + s.getGender() + "," 
                    + s.getAge() + "," + s.getScore() + "\n");
            }
        } catch (IOException e) {
            System.out.println("保存文件时出错: " + e.getMessage());
        }
    }
    
    // 从文件加载学生数据
    public static Vector<Student> loadStudentsFromFile(String filePath) {
        Vector<Student> students = new Vector<>();
        try (Scanner fileScanner = new Scanner(new java.io.File(filePath), "UTF-8")) {
            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine();
                String[] parts = line.split(",");
                if (parts.length == 5) {
                    students.add(new Student(
                        parts[0], // id
                        parts[1], // name
                        parts[2], // gender
                        Integer.parseInt(parts[3]), // age
                        Double.parseDouble(parts[4]) // score
                    ));
                }
            }
        } catch (IOException e) {
            System.out.println("读取文件时出错: " + e.getMessage());
        }
        catch (NumberFormatException e) {
            System.out.println("文件数据格式错误: " + e.getMessage());
        }
        return students;
    }
    
    // 重写toString方法，格式化输出学生信息
    @Override
    public String toString(){
        return String.format("学号：%s | 姓名：%s | 性别：%s | 年龄：%d | 分数：%.1f",
                id, name, gender, age, score);
    }
    
    // Getter方法
    public String getID(){return id;}
    public String getName(){return name;}
    public String getGender(){return gender;}
    public int getAge(){return age;}
    public double getScore(){return score;}
    
    // 显示学生信息表格
    public static void showTable(Vector<Student> students){
        // 设置表格格式
        String format = "| \u001B[36m%-12s\u001B[0m | \u001B[33m%-8s\u001B[0m | %-4s | %4d | \u001B[32m%5.1f\u001B[0m |\n";
        
        // 打印表头
        System.out.println("\u001B[34m+--------------+----------+------+------+-------+\u001B[0m");
        System.out.println("|    \u001B[35m学号\u001B[0m      |   \u001B[35m姓名\u001B[0m   | \u001B[35m性别\u001B[0m | \u001B[35m年龄\u001B[0m |  \u001B[35m成绩\u001B[0m |");
        System.out.println("\u001B[34m+--------------+----------+------+------+-------+\u001B[0m");
        
        // 打印每行数据
        for(Student s : students){
            System.out.printf(format, 
                s.getID(), 
                s.getName(), 
                s.getGender(), 
                s.getAge(), 
                s.getScore());
            System.out.println("\u001B[34m+--------------+----------+------+------+-------+\u001B[0m");
        }
    }
    
    // 追加学生信息到文件
    public static void appendStudentToFile(Student student) {
        try (java.io.OutputStreamWriter writer = new java.io.OutputStreamWriter(
                new java.io.FileOutputStream("students.txt", true), StandardCharsets.UTF_8)) {
            writer.write(student.getID() + "," + student.getName() + "," + student.getGender() + "," 
                + student.getAge() + "," + student.getScore() + "\n");
        } catch (IOException e) {
            System.out.println("保存文件时出错: " + e.getMessage());
        }
    }
    
    // 生成测试数据
    public static Vector<Student> generateSampleData(int count) {
        Vector<Student> data = new Vector<>();
        String[] surnames = {"王","李","张","刘","陈","杨","赵","黄","周","吴"};
        String[] names = {"伟","芳","娜","敏","静","杰","强","磊","军","艳"};
        
        for(int i=1; i<=count; i++){
            String id = "2024" + String.format("%03d", i);
            String name = surnames[i%10] + names[i%10];
            String gender = (i%2 == 0) ? "女" : "男";
            int age = 18 + i%5;
            double score = (5.0 - (i%10)*0.5);
            
            data.add(new Student(id, name, gender, age, Math.max(score, 0)));
        }
        return data;
    }
}


