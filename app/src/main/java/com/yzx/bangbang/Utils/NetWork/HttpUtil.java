package com.yzx.bangbang.Utils.NetWork;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
//已过时
public class HttpUtil {

    URL url;
    URLConnection conn;     //为什么要static ?

    public void  Send(String ip, String toSend, String webServlet) throws IOException {
        url = new URL("http://"+ip+":8080/BangBang/"+webServlet);

         conn = url.openConnection();
        conn.setDoOutput(true);
        OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
        writer.write(toSend);
        writer.close();
    }
    public String Receive() throws IOException {

        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));

        String returnString,received = "";
        while ((returnString = in.readLine()) != null)
            received = returnString;         //接收   是否可以省略returnString ?

        return received;
    }
}
