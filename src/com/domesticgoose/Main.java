package com.domesticgoose;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.stream.Collectors;

public class Main {
    private static ServerSocket ss;
    private static Robot robot;

    private static int port;

    public static void main(String[] args) throws IOException, AWTException, InterruptedException {
        if (args.length > 0) {
            port = Integer.parseInt(args[0]);
        } else {
            port = 80;
        }
        ss = new ServerSocket(port);
        robot = new Robot();

        startupGraphics();
        execute();
    }

    private static void execute() throws IOException, InterruptedException {
        while (true) {
            Socket socket = ss.accept();

            try (DataInputStream dis = new DataInputStream(socket.getInputStream());
                 InputStreamReader in = new InputStreamReader(dis, StandardCharsets.UTF_8)) {

                char[] chars = new char[128];
                in.read(chars);

                String str = String.valueOf(chars);
                str = str.lines().limit(2).collect(Collectors.joining("\n"));
                System.out.println(str);

                String method = determineMethod(str);
                String path = determinePath(str);

                switch (method) {
                    case "GET":
                        handleGetReq(socket);
                        break;
                    case "POST":
                        handlePostReq(robot, path);
                        break;
                }
            }
            socket.close();
        }
    }

    private static void startupGraphics() {
        System.out.println(
                "╔═══╗╔═══╗╔════╗     ╔═══╗╔═══╗╔═╗╔═╗╔═══╗╔════╗╔═══╗\n" +
                "║╔═╗║║╔═╗║║╔╗╔╗║     ║╔═╗║║╔══╝║║╚╝║║║╔═╗║║╔╗╔╗║║╔══╝\n" +
                "║╚═╝║║╚═╝║╚╝║║╚╝     ║╚═╝║║╚══╗║╔╗╔╗║║║ ║║╚╝║║╚╝║╚══╗\n" +
                "║╔══╝║╔══╝  ║║  ╔═══╗║╔╗╔╝║╔══╝║║║║║║║║ ║║  ║║  ║╔══╝\n" +
                "║║   ║║    ╔╝╚╗ ╚═══╝║║║╚╗║╚══╗║║║║║║║╚═╝║ ╔╝╚╗ ║╚══╗\n" +
                "╚╝   ╚╝    ╚══╝      ╚╝╚═╝╚═══╝╚╝╚╝╚╝╚═══╝ ╚══╝ ╚═══╝\n"
        );

        System.out.println("Listening on " + getIP() + ":" + port);
        System.out.println("Ready to accept Connections...");
    }

    private static void handlePostReq(Robot robot, String path) throws InterruptedException {
        switch (path) {
            case "/NEXT":
                robot.keyPress(KeyEvent.VK_RIGHT);
                Thread.sleep(100);
                robot.keyRelease(KeyEvent.VK_RIGHT);
                break;
            case "/PREV":
                robot.keyPress(KeyEvent.VK_LEFT);
                Thread.sleep(100);
                robot.keyRelease(KeyEvent.VK_LEFT);
                break;
        }
    }

    private static void handleGetReq(Socket s) throws IOException {
        try (OutputStreamWriter outputStreamWriter = new OutputStreamWriter(s.getOutputStream(), StandardCharsets.UTF_8);
             BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);
             InputStream resourceAsStream = ClassLoader.getSystemClassLoader().getResourceAsStream("index.html");
             InputStreamReader ir = new InputStreamReader(resourceAsStream, StandardCharsets.UTF_8)) {

            char[] charBuffer = new char[2048];
            ir.read(charBuffer);
            bufferedWriter.write("HTTP/2 200 OK\n" +
                    "content-type: text/html; charset:utf-8\n\n");
            bufferedWriter.write(String.valueOf(charBuffer).trim());
            bufferedWriter.flush();
        }
    }

    private static String determinePath(String str) {
        return str.substring(0, str.indexOf("\n")).split(" ")[1].toUpperCase();
    }

    private static String determineMethod(String str) {
        return str.substring(0, str.indexOf("\n")).split(" ")[0].toUpperCase();
    }

    private static String getIP() {
        String ip;
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface iface = interfaces.nextElement();
                if (iface.isLoopback() || !iface.isUp())
                    continue;

                Enumeration<InetAddress> addresses = iface.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress addr = addresses.nextElement();
                    ip = addr.getHostAddress();
                    if (iface.getDisplayName().equalsIgnoreCase("en0")
                            && ip.contains(".")) {
                        return ip;
                    }
                }
            }
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }
        return null;
    }
}