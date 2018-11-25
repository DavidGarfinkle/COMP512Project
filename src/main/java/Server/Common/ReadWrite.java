package Server.Common;

import java.io.*;

public class ReadWrite implements Serializable
{
	protected String masterRecord = ""; 
    private static String PATH = "/";

    public ReadWrite(String path) {
        PATH = path;
    }

    public void writeObject(RMHashMap d_object, String subPath) {
        try (ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(PATH + subPath))) {
            Trace.info("RM::writeObject(" + PATH + subPath + ") called--");
            os.writeObject(d_object);
        } catch (IOException e) {
            Trace.warn("RM::writeObject(" + PATH + subPath + ") failed--"+ e);
        }
    }

    public RMHashMap readObject(String subPath) {
        try (ObjectInputStream is = new ObjectInputStream(new FileInputStream(PATH + subPath))) {
            Trace.info("RM::readObject(" + PATH + subPath + ") called--");
            return (RMHashMap) is.readObject();
        } catch (Exception e) {
            Trace.warn("RM::readObject(" + PATH + subPath + ") failed--"+ e);
            return null;
        }
    }
}