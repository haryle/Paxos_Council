package utils;

public class AsyncMessageParser {
    public StringBuilder builder;


    public AsyncMessageParser() {
        builder = new StringBuilder();
    }

    public String toString() {
        return builder.toString();
    }

    public String[] append(String str) {
        String msg = builder.append(str).toString();
        if (msg.contains(Message.delimiter)) {
            String[] retVal = msg.split(Message.delimiter);
            for (int i = 0; i < retVal.length; i++){
                retVal[i] = retVal[i].replace("\n","").replace("\r","");
            }
            if (!msg.endsWith(Message.delimiter)) {
                String[] actualRetVal = new String[retVal.length - 1];
                System.arraycopy(retVal, 0, actualRetVal, 0, retVal.length - 1);
                builder = new StringBuilder(retVal[retVal.length - 1]);
                return actualRetVal;
            } else {
                builder.setLength(0);
                return retVal;
            }
        } else
            return null;
    }
}
