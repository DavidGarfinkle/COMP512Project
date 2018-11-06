package Server.LockManager;

import java.util.*;

import Server.Common.*;

public class RMHashtable extends Hashtable<Integer, Vector<ResourceManager>>  {

  public void put(int key, ResourceManager[] rms) throws NullPointerException{

    // Instantiate a new resource manager vector if one does not already exist
    if (super.get(key) == null) {
      Vector<ResourceManager> vector = new Vector<ResourceManager>();
      super.put(key, vector);
    }

    // Add the resource managers which do not already exist
    for (ResourceManager rm : rms) {
        if (!super.get(key).contains(rm)) {
            super.get(key).add(rm);
        }
    }
  }

}
