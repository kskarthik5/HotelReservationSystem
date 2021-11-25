import java.sql.*;
import java.io.*;
class HMS
{
    private static BufferedReader in=new BufferedReader(new InputStreamReader(System.in));
    public static void main(String args[])
    {
        try
        {
            Class.forName("org.sqlite.JDBC");
            Connection
            con=DriverManager.getConnection("jdbc:sqlite:hotel.db","root","");
            outer:while(true){
                System.out.println("Choose an option");
                System.out.println("1. New Guest\n2. Display Available Rooms\n3. Checkout\n4. Query Guest Status\n5. Exit");
                switch(Integer.parseInt(in.readLine())){
                    case 1:
                    System.out.println("1. Family / 2. Individual ?");
                    int temp=Integer.parseInt(in.readLine());
                    if(temp==1){
                        bookRoomforFamily(con);
                    }
                    else{
                        bookRoomforIndividual(con);
                    }
                    break;
                    case 2:
                    Statement stmt=con.createStatement();
                    ResultSet rs2=stmt.executeQuery("SELECT \"ROOMNO\",\"TYPE\",\"RATE\" FROM ROOM WHERE \"STATUS\" is NULL");
                    System.out.println("ROOMNO\tTYPE\tRATE");
                    while(rs2.next())
                    System.out.println(rs2.getInt(1)+"\t"+rs2.getString(2)+"\t"+rs2.getInt(3));
                    break;
                    case 3:
                    checkOut(con);
                    break;
                    case 4:
                    inner:while(true){
                        Statement stmt2=con.createStatement();
                        System.out.println("Enter GUESTID");
                        int GID=Integer.parseInt(in.readLine());
                        ResultSet rs3=stmt2.executeQuery("SELECT COUNT(*) FROM GUEST WHERE GUESTID="+GID);
                        if(rs3.getInt(1)==0)
                        {
                            System.out.println("INVALID GUESTID");
                            continue inner;
                        }
                        rs3=stmt2.executeQuery("SELECT * FROM GUEST WHERE GUESTID="+GID);
                        String status=rs3.getString(5);
                        if(status.equals("CHECKED OUT"))
                        System.out.println("CHECKED OUT");
                        else
                        System.out.println("CHECKED IN. ROOM NO. = "+rs3.getInt(4));
                        break inner;
                    }
                    break;
                    case 5:
                    con.close();
                    break outer;
                }
            }
        }
        catch(Exception e)
        {
            System.out.println(e);
        }
    }
    private static void bookRoomforFamily(Connection con){
        try{
            System.out.println("Enter family head name");
            String familyhead=in.readLine();
            System.out.println("Enter Phone no.");
            String phone=in.readLine();
            Statement stmt=con.createStatement();
            stmt.executeUpdate("INSERT INTO FAMILY (\"Family_Head_Name\",\"Phone_No\") VALUES (\""+familyhead+"\",\""+phone+"\")");
            ResultSet rs=stmt.executeQuery("select last_insert_rowid();");
            int SSN=rs.getInt(1);
            int i=1;
            while(true){
                System.out.println("Enter Details for family member "+i);
                i++;
                System.out.println("Enter Name");
                String name=in.readLine();
                System.out.println("Enter Age");
                int age=Integer.parseInt(in.readLine());
                stmt.executeUpdate("INSERT INTO FAMILYMEMBERS VALUES (\""+SSN+"\",\""+name+"\",\""+age+"\")");
                System.out.println("Enter 1 to add more family members");
                if(in.readLine().charAt(0)!='1')
                break;
            }
            System.out.println("Select a room");
            ResultSet rs2=stmt.executeQuery("SELECT \"ROOMNO\",\"TYPE\",\"RATE\" FROM ROOM WHERE \"STATUS\" is NULL");
            System.out.println("ROOMNO\tTYPE\tRATE");
            while(rs2.next())
            System.out.println(rs2.getInt(1)+"\t"+rs2.getString(2)+"\t"+rs2.getInt(3));
            System.out.println("Enter room no. to be reserved");
            int rid=Integer.parseInt(in.readLine());
            System.out.println("Enter Date");
            String date=in.readLine();
            stmt.executeUpdate("INSERT INTO GUEST (\"SSN\",\"TYPE\",\"ROOMNO\",\"STATUS\",\"CHECKIN\") VALUES (\""+SSN+"\",\"FAMILY\",\""+rid+"\",\"CHECKED IN\",\""+date+"\")");
            ResultSet rs3=stmt.executeQuery("select last_insert_rowid()");
            int GID=rs3.getInt(1);
            stmt.executeUpdate("UPDATE ROOM SET STATUS=\"OCCUPIED\",\"GUESTID\"="+GID+" WHERE ROOMNO="+rid);
            System.out.println("\nRoom No. "+rid+" booked successfully for GuestID "+GID);
        }
        catch(Exception e){
            System.out.println(e);
        }
    }

    private static void bookRoomforIndividual(Connection con){
        try{
            System.out.println("Enter name");
            String name=in.readLine();
            System.out.println("Enter Age");
            int age=Integer.parseInt(in.readLine());
            Statement stmt=con.createStatement();
            stmt.executeUpdate("INSERT INTO INDIVIDUAL (\"Name\",\"Age\") VALUES (\""+name+"\","+age+")");
            ResultSet rs=stmt.executeQuery("select last_insert_rowid();");
            int SSN=rs.getInt(1);
            System.out.println("Select a room");
            ResultSet rs2=stmt.executeQuery("SELECT \"ROOMNO\",\"TYPE\",\"RATE\" FROM ROOM WHERE \"STATUS\" is NULL");
            System.out.println("ROOM NO.\tTYPE\tRATE");
            while(rs2.next())
            System.out.println(rs2.getInt(1)+"\t"+rs2.getString(2)+"\t"+rs2.getInt(3));
            System.out.println("Enter room no. to be reserved");
            int rid=Integer.parseInt(in.readLine());
            System.out.println("Enter Date");
            String date=in.readLine();
            stmt.executeUpdate("INSERT INTO GUEST (\"SSN\",\"TYPE\",\"ROOMNO\",\"STATUS\",\"CHECKIN\") VALUES (\""+SSN+"\",\"INDIVIDUAL\",\""+rid+"\",\"CHECKED IN\",\""+date+"\")");
            ResultSet rs3=stmt.executeQuery("select last_insert_rowid()");
            int GID=rs3.getInt(1);
            stmt.executeUpdate("UPDATE ROOM SET STATUS=\"OCCUPIED\",\"GUESTID\"="+GID+" WHERE ROOMNO="+rid);
            System.out.println("\nRoom No. "+rid+" booked successfully for GuestID "+GID+"\n");
        }
        catch(Exception e){
            System.out.println(e);
        }
    }

    private static void checkOut(Connection con){
        try{
            Statement stmt=con.createStatement();
            System.out.println("Enter GUESTID");
            int GID=Integer.parseInt(in.readLine());
            ResultSet rs=stmt.executeQuery("SELECT COUNT(*) FROM GUEST WHERE GUESTID="+GID);
            if(rs.getInt(1)==0)
            {
                System.out.println("Invalid GUESTID");
                return;
            }
            rs=stmt.executeQuery("SELECT * FROM GUEST WHERE GUESTID="+GID);
            int rno=rs.getInt(4);
            String gtype=rs.getString(3);
            String stat=rs.getString(5);
            if(stat.equals("CHECKED IN")==false){
                System.out.println("GUEST already checked out");
                return;
            }
            System.out.println("GUEST CHECKED IN on date "+rs.getString(6)+". Enter 1 to confirm check out");
            if(in.readLine().charAt(0)!='1')
            return;
            System.out.println("Enter Check out Date");
            String date=in.readLine();
            System.out.println("Enter amount paid");
            int amount=Integer.parseInt(in.readLine());
            System.out.println("Any note to add?");
            String note=in.readLine();
            stmt.executeUpdate("UPDATE GUEST SET STATUS=\"CHECKED OUT\",CHECKOUT=\""+date+"\" WHERE GUESTID="+GID);
            stmt.executeUpdate("UPDATE ROOM SET STATUS=NULL,GUESTID=NULL WHERE ROOMNO="+rno);
            System.out.println("\nGUEST "+GID+" CHECKED OUT SUCCESSFULLY\n");
            stmt.executeUpdate("INSERT INTO BILL(NOTE,AMOUNT,GUESTID,DATE) VALUES(\""+note+"\","+amount+","+GID+",\""+date+"\")");
        }
        catch(Exception e){
            System.out.println(e);
        }
    }
}
