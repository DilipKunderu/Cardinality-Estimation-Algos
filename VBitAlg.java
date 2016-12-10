package vbitalg;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
//import java.util.zip.CRC32;

public class VBitAlg {
    static int[] B;
    static final int mainMapSize = 8000000;
    static final int virtualMapSize = 2500;
    
    private static int hash(String input) throws NoSuchAlgorithmException {
	int result;
	byte[] byteArr;
	MessageDigest md = MessageDigest.getInstance("MD5");
	md.reset();
	md.update(input.getBytes());
	byteArr = md.digest();
	result = getInt(byteArr);
	return result;
    }
    
    private static int getInt(byte[] bytes) {
	return bytes[0] << 24 | (bytes[1] & 0xFF) << 16 | (bytes[2] & 0xFF) << 8 | (bytes[3] & 0xFF);
    }
    
    public static void main(String[] args) throws FileNotFoundException, IOException, NoSuchAlgorithmException {
        BufferedWriter bw;
        Map<String, List<String>> myMap;
        List<Double> vsList = new ArrayList<>();
        List<Integer> actual = new ArrayList<>();
        
        Map<String, ArrayList<Integer>> offlineMap;
        ArrayList<Integer> Xsrc;
        
        try ( 
                BufferedReader br = new BufferedReader (new FileReader("/home/dilip/NetBeansProjects/VBitAlg/src/vbitalg/FlowTraffic.txt"))) {
            bw = new BufferedWriter (new FileWriter("/home/dilip/NetBeansProjects/VBitAlg/src/vbitalg/Output.csv"));
            myMap = new HashMap<>();
            String s1, s2;
            B = new int[mainMapSize];
            List<String> myList;
            br.readLine();
            
            while ((s1 = br.readLine()) != null) {
                s2 = s1.trim().replaceAll(" +", " ");
                String[] tokens = s2.split(" ");
                if (!myMap.containsKey(tokens[0])) {
                    myList = new ArrayList<>();
                    myList.add(tokens[1]);
                    myMap.put(tokens[0],myList);
                } else myMap.get(tokens[0]).add(tokens[1]);
            }
        }

        int[] R = new int[virtualMapSize];        
        List<Integer> randomList = new ArrayList<>();
        
        for (int i = 0; i < R.length; i++) 
            randomList.add(i);
        
        Collections.shuffle(randomList);
        
        for (int i = 0; i < R.length; i++)
            R[i] = randomList.get(i);

        Iterator mapIt = myMap.entrySet().iterator();
        Iterator listIt;

        offlineMap = new HashMap<>();
        Random rand = new Random();
        while (mapIt.hasNext()) {
            Map.Entry pair = (Map.Entry)mapIt.next();
            int pl = hash(pair.getKey().toString());
           
            List subList = (ArrayList)pair.getValue();
            actual.add(subList.size());
            listIt = subList.iterator();
            
            Xsrc = new ArrayList<>();
            while(listIt.hasNext()) {
                int temp1, temp2, temp3;
                
                temp1 = hash((String)listIt.next());
                temp2 = (int)Math.abs(hash(String.valueOf(pl ^ R[Math.abs(temp1) % virtualMapSize])));
                B[temp2 % mainMapSize] = 1;
                int num = Math.abs(rand.nextInt());
                temp3 = hash(String.valueOf(pl ^ R[ num % virtualMapSize]));
                Xsrc.add(temp3 % mainMapSize);
            }
            offlineMap.put((String) pair.getKey(),Xsrc);
        }
 
        int m = 0;
 
        for (int i = 0; i < mainMapSize; i++) {
            if(B[i] == 0)
                m++;
        }
        double Vm;
        double temporary = ((double) m/mainMapSize);
        if (temporary == 0.0)
            Vm = mainMapSize;
        else
            Vm = temporary;
        //System.out.println("Vm " + Vm);
        
        Iterator offIt = offlineMap.entrySet().iterator();
        
        while (offIt.hasNext()) {
            Map.Entry pair = (Map.Entry)offIt.next();
            
            List<Integer> l22 = (ArrayList) pair.getValue();
            int count = 0;
            Integer input;
            int t;
            
            for ( int i = 0; i < virtualMapSize; i++ ) {
                t = hash((String) pair.getKey());
                input = (int) (t ^ R[i]);
                String str = input.toString();
                t = hash(str);
                if ( B[Math.abs((int) t % mainMapSize)] == 0) {
                    count++;
                }
            }

            double Vs = (double) count/virtualMapSize;
            if(Vs == 0.0)
                vsList.add(0.0);
            else vsList.add(Vs);
            //System.out.println("Vs " + Vs);
        }
        
        StringBuilder sb;
        for (int i = 0; i < offlineMap.size(); i++) {
            sb = new StringBuilder("");
            sb.append(actual.get(i));
            sb.append(",");
            double spread = ((double) virtualMapSize * (Math.log(Vm/vsList.get(i))));
            if (spread < 0.0)
                spread = (double) 0.0;
            sb.append(String.format("%.9f", spread));
            bw.write(sb.toString());
            bw.newLine();
            bw.flush();
        }
    }
}