package com.upc.EasyProduction.DataPackages;

import android.util.Pair;

import java.util.Arrays;
import java.util.LinkedList;

public class GVarsData {

    private LinkedList<String> all_names = new LinkedList<String>();
    private LinkedList<String> values = new LinkedList<String>();
    private LinkedList<String> names_by_user = new LinkedList<String>();
    private LinkedList<String> names = new LinkedList<String>();
    private LinkedList<Integer> names_indices = new LinkedList<Integer>();

    public void updateDataNames(byte[] body){
        // we have to be sure that all names entered by user are valid variables names

        String aux = new String(body);

        all_names = new LinkedList(Arrays.asList(aux.split("\n")));

        names.clear();

        for (int i = 0; i < all_names.size(); i++){ // i iterate all names because i want to know the order of names vars, independent of the user's order...
            // maybe it could be done more efficiently, with a linked list of pairs string, integer and the sorting this list
            // in this way we have only to iterate names list and no all_names, but we have to sort the list by index...
            if(names_by_user.contains(all_names.get(i))){
                names.add(all_names.get(i));
                names_indices.add(i);
            }
        }

    }

    public void updateDataValues(byte[] body){
        values.clear();
    }

    public String[] getVarsNames(){
        return names.toArray(new String[names.size()]);
    }

    public void setVarNames(LinkedList<String> names){
        names_by_user = names;
    }



}
