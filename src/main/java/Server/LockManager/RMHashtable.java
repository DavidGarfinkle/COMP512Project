package Server.LockManager;

import java.util.*;

import Server.Common.*;
import Server.Interface.*;

public class RMHashtable extends Hashtable<Integer, Vector<IResourceManager>>  {

  public void put(int key, IResourceManager rm) throws NullPointerException{

    // Instantiate a new resource manager vector if one does not already exist
    if (super.get(key) == null) {
      Vector<IResourceManager> vector = new Vector<IResourceManager>();
      super.put(key, vector);
    }

    // Add the resource managers which do not already exist
    if (!super.get(key).contains(rm)) {
        super.get(key).add(rm);
    }
  }

}
