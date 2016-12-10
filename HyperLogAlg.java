package hyperlogalg;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.zip.CRC32;

public class HyperLogAlg {
    static final int m = 64;
    static final int b = 6;
    static final double alpha = 0.709;
    
    private static long convertIPAddress(String s) throws NumberFormatException {
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
    
    private static int hash(long long1) {
        CRC32 crc = new CRC32();
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        buffer.putLong(long1);
        crc.update(buffer.array());
        int index = (int)crc.getValue();
        return Math.abs(index);
    }

    public static void main(String[] args) throws IOException {
        BufferedWriter bw;
        Map<String, List<String>> myMap;
        
        try ( 
                BufferedReader br = new BufferedReader (new FileReader("/home/dilip/NetBeansProjects/HyperLogAlg/src/hyperlogalg/FlowTraffic.txt"))) {
            bw = new BufferedWriter (new FileWriter("//home/dilip/NetBeansProjects/HyperLogAlg/src/hyperlogalg/Output.csv"));
            myMap = new HashMap<>();
            String s1, s2;
            List<String> myList;
            
            br.readLine();
            //fill the hash map
            while ((s1 = br.readLine()) != null) {
                s2 = s1.trim().replaceAll(" +", " ");
                String[] tokens = s2.split(" ");
                if (!myMap.containsKey(tokens[0])) {
                    myList = new ArrayList<>();
                    myList.add(tokens[1]);
                    myMap.put(tokens[0],myList);
                } else myMap.get(tokens[0]).add(tokens[1]);
            }
            
            Iterator mapIt = myMap.entrySet().iterator();
            Iterator listIt;
            
            while (mapIt.hasNext()) {
                StringBuilder sb = new StringBuilder("");
                Map.Entry pair = (Map.Entry)mapIt.next();
           
                List subList = (ArrayList)pair.getValue();
                sb.append(subList.size());
                sb.append(",");
                listIt = subList.iterator();
                int[] registers = new int[m];
                
                while(listIt.hasNext()) {
                    long temp = convertIPAddress((String)listIt.next());
                    String s = Integer.toBinaryString(hash(temp));
                    int referIndex = s.length() - b;
                    String rightString = s.substring(referIndex);
                    int bitmapindex = Integer.parseInt(rightString, 2);
                    
                    String leftString = s.substring(0, b);
                    int run_length = Integer.numberOfTrailingZeros(Integer.parseInt(leftString));
                    registers[bitmapindex] = Math.max(registers[bitmapindex], run_length + 1);   
                }
                
                double DV = 0.0;
                double sumValue=0.0;
                
                for(int i=0;i<registers.length;i++)
                    sumValue += Math.pow(2, -registers[i]);
    	
                double DV_est = alpha * Math.pow(m, 2)/sumValue;
                double cal = (double) DV_est/Math.pow(2, 32);
                    
                if(DV_est < (5/2) * m) {
                    int count=0;
                    for(int j=0;j<registers.length;j++) {
                        if(registers[j]==0)
                            count++;
                    }
                    if(count == 0)
                        DV=DV_est;
                    else {
                        double val = (double) m/count;       				
                        DV = m * Math.log(val);
                    }
                }
                else if(DV_est <= (1/30)*Math.pow(2, 32)) 
                    DV = DV_est;
                    
                else if(DV_est > (1/30) * Math.pow(2, 32))
                    DV = -Math.pow(2, 32) * Math.log(1-cal); 
                    
                sb.append(String.format("%.9f",DV));
                bw.write(sb.toString());
                bw.newLine();
                bw.flush();
            }
            bw.close();
        }
    }
}
