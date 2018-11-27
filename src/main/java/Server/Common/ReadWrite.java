package Server.Common;

import java.io.*;

public class ReadWrite implements Serializable
{
	protected String masterRecord = ""; 
    private static String PATH = "/";

    public ReadWrite(String path) {
        PATH = path;
    }

    // TODO: Move traces to readwrite
    public void writeObject(RMHashMap d_object, String subPath)
    {
        try (ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(PATH + subPath))) {
            os.writeObject(d_object);
        } catch (IOException e) {
            System.err.println(e);
        }
    }

    public RMHashMap readObject(String subPath) throws ClassNotFoundException, IOException
    {
        ObjectInputStream is = new ObjectInputStream(new FileInputStream(PATH + subPath));
        return (RMHashMap) is.readObject();
    }
}