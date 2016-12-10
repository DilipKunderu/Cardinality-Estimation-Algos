package probabcountalg;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ProbabCountAlg {
    
    private static long convertIPAddress(String s) throws NumberFormatException {
		// TODO Auto-generated method stub
        int[] octets = new int[4];
        String[] ip_octet = s.split("\\.");
        for (int i = 0; i < 4; i++) {
            octets[i] = Integer.parseInt(ip_octet[i]);
        }
		
        long IP_Address = (long) 0;
        for (int i = 0, j = 3; i < 4; i++, j--) {
            IP_Address += (long) (octets[i] * Math.pow(256, j));
        }
        return IP_Address;
    }
    
    private static void hashing(int[] arr, long long1) {
		// TODO Auto-generated method stub
        int index = (int) (long1 % arr.length);
        arr[index] = 1;
    }
    
    public static void main(String[] args) throws FileNotFoundException, IOException {
        // TODO code application logic here
        BufferedReader br = new BufferedReader (new FileReader("/home/dilip/NetBeansProjects/ProbabCountAlg/src/probabcountalg/FlowTraffic.txt"));
        BufferedWriter bw = new BufferedWriter (new FileWriter("/home/dilip/NetBeansProjects/ProbabCountAlg/src/probabcountalg/Output.txt"));
        Map<String, List<String>> myMap = new HashMap<>();
     
        Iterator itr;
        String s1, s2;
        
        br.readLine();
        
        while ((s1 = br.readLine()) != null) {
            s2 = s1.trim().replaceAll(" +", " ");
            String[] tokens = s2.split(" ");
            if (!myMap.containsKey(tokens[0])) {
                List<String> myList = new ArrayList<>();
                myList.add(tokens[1]);
                myMap.put(tokens[0],myList);
            } else myMap.get(tokens[0]).add(tokens[1]);
        }
        br.close();
        
        for(Map.Entry<String, List<String>> entry : myMap.entrySet()) {
            int[] bitmap = new int[100];
            bw.write(String.valueOf(entry.getValue().size()));
            bw.write(",");
                  
            itr = entry.getValue().iterator();
              
            while (itr.hasNext()) 
                hashing(bitmap, convertIPAddress((String) itr.next()));
                
            int estimated = 0;
            
            for (int i = 0; i < bitmap.length; i++) {
                if(bitmap[i] == 0)
                    estimated++;
            }
                
            double temp = (double) estimated/bitmap.length;
            double estimate = -bitmap.length * Math.log(temp);
            bw.write(String.format("%.9f", estimate));
            
            bw.newLine();
        }
        bw.flush();
        bw.close();
    }
}
