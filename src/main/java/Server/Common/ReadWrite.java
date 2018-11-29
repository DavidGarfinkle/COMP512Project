package Server.Common;

import java.io.*;

public class ReadWrite implements Serializable
{
	protected String masterRecord = "";
    private static String PATH = "/";

    public ReadWrite(String path) {
        PATH = path;
        new File(PATH).mkdir();
    }

    public void writeObject(Object d_object, String subPath) {
        String fullPath = PATH + "/" + subPath;
        try (ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(fullPath))) {
            Trace.info("RM::writeObject(" + fullPath + ") called--");
            os.writeObject(d_object);
        } catch (IOException e) {
            Trace.warn("RM::writeObject(" + fullPath + ") failed--"+ e);
        }
    }

    public Object readObject(String subPath) {
        String fullPath = PATH + "/" + subPath;
        try (ObjectInputStream is = new ObjectInputStream(new FileInputStream(fullPath))) {
            Trace.info("RM::readObject(" + fullPath + ") called--");
            return is.readObject();
        } catch (Exception e) {
            Trace.warn("RM::readObject(" + fullPath + ") failed--"+ e);
            return null;
        }
    }

		public void deleteFile(String subPath) {
				String fullPath = PATH + "/" + subPath;
        File file = new File(fullPath);
        if(file.delete()){
		        System.out.println(fullPath + " deleted");
        } else {
						System.out.println(fullPath + " doesn't exists");
				}
		}
}
