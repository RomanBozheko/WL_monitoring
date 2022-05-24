import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class Main {
    private static List<Thread> mThreadList;

    public static void main(String[] args) throws IOException, InterruptedException {


        mThreadList = new ArrayList<Thread>();


        try {

            FileInputStream fstream = new FileInputStream("ids");
            BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
            String Line;

            for (int i = 0; i < Check.countUid(); i++) {
                while ((Line = br.readLine()) != null) {

                    String delimeter = " ";
                    String[] res = Line.split(delimeter);
                    String uid = res[0];
                    String ids = res[1];
                    String phone = res[2];
                    String id = res[3];
                    String paymentIp = res[4];
                    String paymentPort = res[5];

                    mThreadList.add(new Thread() {
                        @Override
                        public void run() {
                            try {

                                for (; ; Thread.sleep(1000 * Check.timeCheck())) {
//                                    System.out.println(uid + " " + new Date() + " " + currentThread().getName());

                                    if (!Check.trueFalseWL(uid)) {

                                        System.out.println(currentThread().getName() + " | " + uid + " <--- WO/WL off on RC ---> " + " " + new Date() +
                                                "\nTaxi - " + ids + " || PaymentService > " + paymentIp + ":" + paymentPort + " || Card: " + Check.OnOffCard(uid) + "\n");

                                    } else {
                                        String subject = "Check WO or TNS on " + ids;
                                        String subjectPayment = "Check PaymentService.API on " + ids;
                                        String subjectAll = "Check WO/TNS/PS on " + ids;

                                        String msg = "UID ===> " + uid + "   " + "IDS ===> " + ids + "   " +
                                                "Link ===> " + "https://rainbow.evos.in.ua/ru-RU/" + uid + "/WebOrders" +
                                                " PS link - " + "http://" + paymentIp + ":" + paymentPort + "/api/public/ping";
                                        String msgPayment = "UID ===> " + uid + "   " + "IDS ===> " + ids + " PS link - " +
                                                "http://" + paymentIp + ":" + paymentPort + "/api/public/ping";


                                        String msgTelegram = "Check WO or TNS ===> " + " IDS: " + ids + " UID: |" + uid + "|" + " Phone " + phone +
                                                "| Link ===> " + "https://rainbow.evos.in.ua/ru-RU/" + uid + "/WebOrders";
                                        String msgTelegramPayment = "Check PaymentService.API on ===> " + ids + " UID: |" + uid + "|" + " Phone " + phone +
                                                " |PS link - http://" + paymentIp + ":" + paymentPort + "/api/public/ping";
                                        String msgTelegramAll = "Check WO/TNS/PS ===> " + " IDS: " + ids + " UID: |" + uid + "|" + " Phone " + phone +
                                                "| Link ===> " + "https://rainbow.evos.in.ua/ru-RU/" + uid + "/WebOrders" +
                                                " |PS link - http://" + paymentIp + ":" + paymentPort + "/api/public/ping";

                                        boolean WoTns = Check.reqCostWork(uid);
                                        boolean ps = Check.PingPaymentWork(paymentIp, paymentPort);
//                                        System.out.println(Check.PingPayment("0", "0") + " | <<<<<<<<<<<<");

                                        if (WoTns && ps) {
                                            System.out.println(currentThread().getName() + " | " + uid + " <-----------------------> " + " " + new Date() +
                                                    "\nTaxi - " + ids + " || PaymentService > " + paymentIp + ":" + paymentPort + " || Card: " + Check.OnOffCard(uid) + "\n");
                                        } else if (!WoTns && !ps) {
                                            if (!Check.IsTask(subjectAll)) {
                                                Check.createTask(subjectAll, msg, phone, id);
                                                Check.telegramMsg(msgTelegramAll);
                                            }
                                            System.out.println(currentThread().getName() + " | " + uid + " <----Error WO/TNS/PS----> " + " " + new Date() +
                                                    "\nTaxi - " + ids + " || PaymentService > " + paymentIp + ":" + paymentPort + " || Card: " + Check.OnOffCard(uid) + "\n");
                                        } else if (!ps) {
                                            if (!Check.IsTask(subjectPayment)) {
                                                Check.createTask(subjectPayment, msgPayment, phone, id);
                                                Check.telegramMsg(msgTelegramPayment);
                                            }
                                            System.out.println(currentThread().getName() + " | " + uid + " <--------PS ERROR-------> " + " " + new Date() +
                                                    "\nTaxi - " + ids + " || PaymentService > " + paymentIp + ":" + paymentPort + " || Card: " + Check.OnOffCard(uid) + "\n");
                                        } else if (!WoTns) {
                                            if (!Check.IsTask(subject)) {
                                                Check.createTask(subject, msg, phone, id);
                                                Check.telegramMsg(msgTelegram);
                                            }
                                            System.out.println(currentThread().getName() + " | " + uid + " <--------WO/TNS ERROR---> " + " " + new Date() +
                                                    "\nTaxi - " + ids + " || PaymentService > " + paymentIp + ":" + paymentPort + " || Card: " + Check.OnOffCard(uid) + "\n");
                                        }


                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                    });
                }
            }

            System.out.println("© Made by Roman Bozhenko");
            System.out.println(">> Check in: " + Check.timeCheck() + " sec! <<");
            System.out.println("First > " + Check.timeFirstCheck() + "  Second > " + Check.timeSecondCheck() + "  Third > " + Check.timeThirdCheck());

            for (Thread currentThread : mThreadList) {
                currentThread.start();
            }

            for (; ; Thread.sleep(1000 * Check.timeCheck())) {
                if (!Check.WorkWLS()) {
                    String subject = "WhiteLabel.Server ERROR";
                    String description = "All WL do not work, write in WORK chat || Check here => https://wls.evos.in.ua/index.html";

                    boolean isTask = Check.IsTask(subject);
                    if (!isTask) {
                        Check.createTask(subject, description, "0", "1150");
                        Check.telegramMsg(subject + " < >  " + description);
                    }

                    System.out.println("********************** WLS - Error " + new Date() + " **********************");

                } else {

                    System.out.println("********************** WLS - OK " + new Date() + " *************************");

                }
            }//проверка WLS

        } catch (Exception error) {
            System.out.println("error in file");
        }
    }
}

class Check {
    public static Integer countUid() {
        int res = 0;
        try {

            InputStream is = new BufferedInputStream(new FileInputStream("ids"));
            byte[] c = new byte[1024];

            int count = 0;
            int readChars = 0;
            boolean empty = true;
            while ((readChars = is.read(c)) != -1) {
                empty = false;
                for (int i = 0; i < readChars; ++i) {
                    if (c[i] == '\n') {
                        ++count;

                    }
                }
            }
//            System.out.println(count);
            res = ++count;

        } catch (Exception er) {
            System.out.println("er > " + er);
        }
        return res;
    }

    public static Integer timeCheck() throws IOException {
        int a = 0;

        FileInputStream fileInputStream = new FileInputStream("time");
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));
        String strLine;
        while ((strLine = bufferedReader.readLine()) != null) {
            String delimeter = "/";
            String[] res = strLine.split(delimeter);

            int time = Integer.parseInt(res[0]);
            a = time;
        }
        return a;
    }

    public static Integer timeFirstCheck() throws IOException {
        int first = 0;

        FileInputStream fileInputStream = new FileInputStream("time");
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));
        String strLine;
        while ((strLine = bufferedReader.readLine()) != null) {
            String delimeter = "/";
            String[] res = strLine.split(delimeter);

            int time = Integer.parseInt(res[1]);
            first = time;
        }
        return first;
    }

    public static Integer timeSecondCheck() throws IOException {
        int second = 0;

        FileInputStream fileInputStream = new FileInputStream("time");
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));
        String strLine;
        while ((strLine = bufferedReader.readLine()) != null) {
            String delimeter = "/";
            String[] res = strLine.split(delimeter);

            int time = Integer.parseInt(res[2]);
            second = time;
        }
        return second;
    }

    public static Integer timeThirdCheck() throws IOException {
        int third = 0;

        FileInputStream fileInputStream = new FileInputStream("time");
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));
        String strLine;
        while ((strLine = bufferedReader.readLine()) != null) {
            String delimeter = "/";
            String[] res = strLine.split(delimeter);

            int time = Integer.parseInt(res[3]);
            third = time;
        }
        return third;
    }

    public static Integer WLS() throws IOException {
        int resCode = 0;
        String host = "https://wls.evos.in.ua/api/account/login?DispatchingUid=9c583ddc-e43f-4852-9b76-114227ab6e30&WlVersion=1&OsName=android&OsVersion=9";
        URL obj = new URL(host);
        HttpURLConnection conn = (HttpURLConnection) obj.openConnection();

        try {
            conn.setRequestMethod("GET");
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            resCode = conn.getResponseCode();
//            System.out.println("Code> " + conn.getResponseCode());
        } catch (Exception e) {
            resCode = conn.getResponseCode();
            e.printStackTrace();
//            System.out.println("err -> " + e);
        }
        return resCode;
    }

    public static Boolean WorkWLS() throws IOException, InterruptedException {
        boolean ret = false;
        int WLScheckCode1 = Check.WLS();
        if (WLScheckCode1 != 200) {
            Thread.sleep(1000 * Check.timeFirstCheck());
            int WLScheckCode2 = Check.WLS();

            if (WLScheckCode2 != 200) {
                Thread.sleep(1000 * Check.timeSecondCheck());
                int WLScheckCode3 = Check.WLS();

                if (WLScheckCode3 != 200) {
                    Thread.sleep(1000 * Check.timeThirdCheck());
                    int WLScheckCode4 = Check.WLS();

                    if (WLScheckCode4 != 200) {
                        ret = false;
                    }
                }
            }
        } else {
            ret = true;
        }
        return ret;
    }

    public static Boolean IsTask(String task) throws IOException {
        boolean pp = false;
        String host = "http://desk.evos.in.ua/rest/servicedesk/1/servicedesk/SD/issueli" +
                "st?jql=project+%3D+SD+AND+status+in+(%22In+Progress%22%2C+%22Wait+for+Cus" +
                "tomer%22)+ORDER+BY+created+DESC%2C+assignee+ASC&columnNames=summary&issuesPerPage=500";
//        String host = "https://desk.evos.in.ua/rest/servicedesk/1/servicedesk/SD/issueli" +
//                "st?jql=project+%3D+SD+AND+status+in+(%22In+Progress%22%2C+%22Wait+for+Cus" +
//                "tomer%22)+ORDER+BY+created+DESC%2C+assignee+ASC&columnNames=summary&issuesPerPage=500";
        URL obj = new URL(host);
        HttpURLConnection conn = (HttpURLConnection) obj.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Authorization", "Basic /**/");

        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));

        String inputLine;
        StringBuffer response = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        String json = response.toString();
        int codd = conn.getResponseCode();
//        codd = 400;
        System.out.println(">>>> " + json);
        JSONObject js = new JSONObject(json);
        JSONArray issues = js.getJSONArray("issues");
        for (int i = 0; i < issues.length(); i++) {
            JSONObject iss = issues.getJSONObject(i);
            JSONArray fields = iss.getJSONArray("fields");

            for (int j = 0; j < fields.length(); j++) {
                JSONObject ff = fields.getJSONObject(j);
                String fieldAsHtml = ff.getString("fieldAsHtml");
                int index = fieldAsHtml.indexOf(task, 0);

                if (index >= 0 && codd == 200) {
                    pp = true;
                }
            }
        }
        return pp;
    }

    public static void createTask(String subject, String msg, String phone, String id) throws IOException {
//        String hostPost = "https://desk.evos.in.ua/rest/api/2/issue/";
        String hostPost = "http://desk.evos.in.ua/rest/api/2/issue/";
        URL objPost = new URL(hostPost);
        HttpURLConnection connect = (HttpURLConnection) objPost.openConnection();
        connect.setRequestMethod("POST");
        connect.setRequestProperty("Content-Type", "application/json");
        connect.setRequestProperty("Authorization", "Basic /**/");
        connect.setDoOutput(true);
        DataOutputStream dstream = new DataOutputStream(connect.getOutputStream());
        dstream.writeBytes("{\n" +
                "  \"fields\": {\n" +
                "    \"project\": {\n" +
                "      \"id\": \"10201\"\n" +
                "    },\n" +
                "    \"summary\": \"" + subject + "\",\n" +
                "    \"issuetype\": {\n" +
                "      \"id\": \"10100\"\n" +
                "    },\n" +
                "    \"reporter\": {\n" +
                "      \"name\": \"\"\n" +
                "    },\n" +
                "    \"description\": \"" + msg + "\",  \n" +
                "    \"customfield_10500\": \"" + phone + "\",\n" +
                "    \"customfield_10800\": \"" + id + "\",\n" +
                "    \"customfield_10209\": {\n" +
                "      \"id\": \"10027\",\n" +
                "      \"child\": {\n" +
                "        \"id\": \"10039\"\n" +
                "      }\n" +
                "    }\n" +
                "  }\n" +
                "}");
        dstream.flush();
        dstream.close();
        int responseCode = connect.getResponseCode();
    }

    public static void telegramMsg(String msg) throws IOException {
        URL bot = new URL("https://api.telegram.org/bot789015649:AAEjXfpqLpiqJWf9C9GYBc0MWCgKA9aC1_0/sendMessage?chat_id=-1001467704812&text" + "=" + msg);
        BufferedReader in = new BufferedReader(new InputStreamReader(bot.openStream()));
        in.close();
    }

    public static Integer reqCost(String uid) throws IOException {
        int reqCostCode = 0;


        String hostPost = "https://rainbow.evos.in.ua/proxy/" + uid + "/api/weborders/cost";
        URL objPost = new URL(hostPost);
        HttpURLConnection connect = (HttpURLConnection) objPost.openConnection();
        connect.setRequestMethod("POST");
        connect.setRequestProperty("Content-Type", "application/json");
        connect.setRequestProperty("Authorization", "Basic Z3Vlc3Q6YjBlMGVjN2ZhMGE4OTU3N2M5MzQxYzE2Y2ZmODcwNzg5Mj"
                + "IxYjMxMGEwMmNjNDY1ZjQ2NDc4OTQwN2Y4M2YzNzdhODdhOTdkNjM1Y2FjMjY"
                + "2NjE0N2E4ZmI1ZmQyN2Q1NmRlYTNkNGNlYmExZmM3ZDAyZjQyMmRkYTY3OTRlM2M=");
        connect.setRequestProperty("X-WO-API-APP-ID", "rainbow-taxi");
        connect.setDoOutput(true);
        DataOutputStream dstream = new DataOutputStream(connect.getOutputStream());
        dstream.writeBytes(
                "{\n" +
                        "\t\"reservation\":false\n" +
                        "    ,\"route\":\n" +
                        "       [\n" +
                        "        {\"name\":\"TEST\",\"lat\":1.1, \"lng\":1.1}\n" +
                        "\n" +
                        "        ]\n" +
                        "    ,\"taxiColumnId\":0\n" +
                        "\n" +
                        "}"
        );
        dstream.flush();
        dstream.close();
        reqCostCode = connect.getResponseCode();

//        System.out.println("reqCostCode > " + reqCostCode);
        return reqCostCode;
    }

    public static Boolean reqCostWork(String uid) throws IOException, InterruptedException {
        boolean monitoring = false;
        if (Check.reqCost(uid) != 200) {
//            System.out.println("> 1");
            Thread.sleep(1000 * Check.timeFirstCheck());

//            System.out.println(" <<<<<<<<<<<<<<<<<<<<< reqCostWork 1 >>>>>>>>>>>>>> "+uid);

            if (Check.reqCost(uid) != 200) {
//                System.out.println("> 2");
                Thread.sleep(1000 * Check.timeSecondCheck());
//                System.out.println(" <<<<<<<<<<<<<<<<<<<<< reqCostWork 2 >>>>>>>>>>>>>> "+uid);

                if (Check.reqCost(uid) != 200) {
//                    System.out.println("> 3");
                    Thread.sleep(1000 * Check.timeThirdCheck());
//                    System.out.println(" <<<<<<<<<<<<<<<<<<<<< reqCostWork 3 >>>>>>>>>>>>>> "+uid);

                    if (Check.reqCost(uid) != 200) {
//                        System.out.println(" <<<<<<<<<<<<<<<<<<<<< reqCostWork 4 >>>>>>>>>>>>>> "+uid);
//                        System.out.println("> 4");
                    } else {
                        monitoring = true;
                    }
                } else {
                    monitoring = true;
                }
            } else {
                monitoring = true;
            }

        } else {
            monitoring = true;
        }

        return monitoring;
    }

    public static Boolean trueFalseWL(String uid) throws IOException {
        boolean wl = false;
        String host = "https://wls.evos.in.ua/api/account/login?DispatchingUid=" + uid + "&WlVersion=1&OsName=android&OsVersion=9";
        URL obj = new URL(host);
        HttpURLConnection conn = (HttpURLConnection) obj.openConnection();

        try {
            conn.setRequestMethod("GET");
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            String input;
            StringBuffer response = new StringBuffer();

            while ((input = in.readLine()) != null) {
                response.append(input);
            }
            in.close();
            String json = response.toString();
            JSONObject js = new JSONObject(json);
            wl = js.getBoolean("canWork");
//            System.out.println(wl+ " |||>>>"+ uid);


        } catch (Exception e) {
//            System.out.println("err -> " + e);
        }
        return wl;
    }

    public static Boolean OnOffCard(String uid) throws IOException {
        boolean card_payment_permitted = false;

        String host = "https://rainbow.evos.in.ua/proxy/" + uid + "/api/settings";
        URL obj = new URL(host);
        HttpURLConnection conn = (HttpURLConnection) obj.openConnection();

        try {
            conn.setRequestMethod("GET");
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            String input;
            StringBuffer response = new StringBuffer();

            while ((input = in.readLine()) != null) {
                response.append(input);
            }
            in.close();
            String json = response.toString();
            JSONObject js = new JSONObject(json);
            card_payment_permitted = js.getBoolean("card_payment_permitted");


        } catch (Exception e) {
//            System.out.println("err -> " + e);
        }

        return card_payment_permitted;
    }

    public static Integer PingPayment(String ip, String port) throws IOException {
        int pingCodd = 0;

        String host = "http://" + ip + ":" + port + "/api/public/ping";
        URL obj = new URL(host);
        HttpURLConnection conn = (HttpURLConnection) obj.openConnection();

        try {
            conn.setRequestMethod("GET");
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            String input;
            StringBuffer response = new StringBuffer();

            while ((input = in.readLine()) != null) {
                response.append(input);
            }
            in.close();
            pingCodd = conn.getResponseCode();

        } catch (Exception e) {
//            System.out.println("err -> " + e);
        }
        return pingCodd;
    }

    public static Boolean PingPaymentWork(String ip, String port) throws IOException, InterruptedException {
        boolean pay = false;

        if (Integer.parseInt(port) != 0) {

            if (Check.PingPayment(ip, port) != 200) {
                Thread.sleep(1000 * Check.timeFirstCheck());
//                System.out.println(" <<<<<<<<<<<<<<<<<<<<< PingPaymentWork 1 >>>>>>>>>>>>>> "+ip+" | "+port);

                if (Check.PingPayment(ip, port) != 200) {
                    Thread.sleep(1000 * Check.timeSecondCheck());
//                    System.out.println(" <<<<<<<<<<<<<<<<<<<<< PingPaymentWork 2 >>>>>>>>>>>>>> "+ip+" | "+port);

                    if (Check.PingPayment(ip, port) != 200) {
                        Thread.sleep(1000 * Check.timeThirdCheck());
//                        System.out.println(" <<<<<<<<<<<<<<<<<<<<< PingPaymentWork 3 >>>>>>>>>>>>>> "+ip+" | "+port);

                        if (Check.PingPayment(ip, port) != 200) {
//                            System.out.println(" <<<<<<<<<<<<<<<<<<<<< PingPaymentWork 4 >>>>>>>>>>>>>> "+ip+" | "+port);
                        } else {
                            pay = true;
                        }
                    } else {
                        pay = true;
                    }
                } else {
                    pay = true;
                }
            } else {
                pay = true;
            }

        } else {
            pay = true;
        }
        return pay;
    }
}