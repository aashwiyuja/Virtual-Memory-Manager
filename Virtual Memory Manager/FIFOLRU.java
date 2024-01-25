import java.io.BufferedReader; 
import java.io.FileReader; 
import java.io.IOException; 
import java.util.ArrayList; 
import java.util.LinkedHashMap; 
import java.util.Scanner;
import java.io.RandomAccessFile;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.logging.Logger;
import java.io.PrintWriter;
import static java.util.logging.Level.SEVERE;
  
public class FIFOLRU { 
    // Number of physical frames 
    private static final int FRAME_SIZE = 128; 
  
    // Create an array of physical frames 
    public static int[] physicalFrames = new int[FRAME_SIZE]; 

    PageTable PT = new PageTable();
    TLB tlb = new TLB(16);
    PhysicalMemory PM;

    // Initialize page fault count 
    int pageFaultCount = 0; 

    //Address variables
    int virAddr;
    int phyAddr = -1;
    int pgNum;
    int offset;
    int frameNo;

    //Used to parse input
    String line;
    char act;
    
    //To measure statistics
    int tlbHit = 0;
    int pgFault = 0;
    int writeBackCount = 0;
    int writeBack = -1;
    
    int clock = 0;
  
    // Constructor for the Page Replacement Algorithm 
    public FIFOLRU(){ 
        for (int i = 0; i < FRAME_SIZE; i++) 
            physicalFrames[i] = -1; 
    } 
  
    // Method to implement FIFO page replacement algorithm 
    /**
     * @param fileName
     * @return
     */
    int [] FIFO(String fileName){ 

        //Loading BACKING_STORE.bin
        RandomAccessFile backStore = null;
        try {backStore = new RandomAccessFile(new File("BACKING_STORE.bin"), "r");}
        catch (FileNotFoundException ex) {Logger.getLogger(VirtualMemoryManager.class.getName()).log(SEVERE, null, ex);}

        try{
            // Create a buffered reader for the file 
            BufferedReader reader = new BufferedReader(new FileReader(fileName)); 

            // Create an array list to store the pages 
            ArrayList<Integer> pageReferenceString = new ArrayList<>(); 

            String line;

            // Read the file line by line 
            while ((line = reader.readLine()) != null){ 
                // Add the page to the array list 
                pageReferenceString.add(Integer.parseInt(line)); 

                // Create a linked hash map to keep track of pages in physical frames 
                LinkedHashMap<Integer, Integer> pageMap = new LinkedHashMap<>(FRAME_SIZE); 
        
                // Iterate over the page reference string 
                for (int i = 0; i < pageReferenceString.size(); i++){ 

                    int page = pageReferenceString.get(i); 

                    virAddr = new Scanner(line).nextInt();

                    //Getting addresses
                    pgNum = virAddr >> 8;
                    offset = virAddr & 0x00FF;

                    // Check if the page is present in physical frames 
                    if (pageMap.containsKey(page)) { 

                        // Increment page hit count 
                        writeBack = pgNum;
                        pageFaultCount++; 
                        tlbHit++;
                    } 
                    else{ 
                        // If the page is not present in the physical frames 
                        if (pageMap.size() == FRAME_SIZE){ 
                            // Get the first page from the linked hash map 
                            int firstPage = pageMap.entrySet().iterator().next().getKey(); 
        
                            // Remove the page from hash map 
                            pageMap.remove(firstPage); 
                        }
        
                        // Add the page to the linked hash map 
                        pageMap.put(page, 0); 

                        phyAddr = offset + (frameNo*256) - 256;
        
                        // Increment page fault count 
                        pageFaultCount++; 
                        clock ++; 
                    } 
                } 

                //Print output
                try{
                    backStore.seek(pgNum * 256);   
                    PrintWriter file = new PrintWriter("correct.txt");
                    
                    for (int i=0; i<=1000; i++){
                        file.println("Virtual address: " + virAddr + "\tPhysical address: " + phyAddr +  "\tValue: " + backStore.readByte()); 
                    }
                    file.close();
                }
                catch (IOException ex) {Logger.getLogger(VirtualMemoryManager.class.getName()).log(SEVERE, null, ex);}
            } 

            // Close the buffered reader 
            reader.close(); 
            writeBack = -1;

            // Print the page fault count 
            System.out.println("FIFO Page Fault Count: " + pageFaultCount); 
            } 
            catch (IOException e){ 
                e.printStackTrace(); 
            }

            //Returning statistics
            int stats[] = {clock, tlbHit, pageFaultCount, writeBackCount};
            return stats;
    } 
  

    
    // Method to implement LRU page replacement algorithm 
    int [] LRU(String fileName){
        //Loading BACKING_STORE.bin
        RandomAccessFile backStore = null;
        try {backStore = new RandomAccessFile(new File("BACKING_STORE.bin"), "r");}
        catch (FileNotFoundException ex) {Logger.getLogger(VirtualMemoryManager.class.getName()).log(SEVERE, null, ex);}

        try{ 
            // Create a buffered reader for the file 
            BufferedReader reader = new BufferedReader(new FileReader(fileName)); 
  
            // Create an array list to store the pages 
            ArrayList<Integer> pageReferenceString = new ArrayList<>(); 
  
            // Read the file line by line 
            while ((line = reader.readLine()) != null){ 
                // Add the page to the array list 
                pageReferenceString.add(Integer.parseInt(line));
                
                // Create a linked hash map to keep track of pages in physical frames 
                LinkedHashMap<Integer, Integer> pageMap = new LinkedHashMap<>(FRAME_SIZE); 
        
                // Iterate over the page reference string 
                for (int i = 0; i < pageReferenceString.size(); i++){ 
                    int page = pageReferenceString.get(i); 
        
                    // Check if the page is present in physical frames 
                    if (pageMap.containsKey(page)){ 
                        // Update the page in linked hash map 
                        pageMap.put(page, i); 
                        tlbHit++;
                    } 
                    else{ 
                        // If the page is not present in physical frames 
                        if (pageMap.size() == FRAME_SIZE){ 
                            int lruPage = 0, lruValue = Integer.MAX_VALUE; 
        
                            // Find the least recently used page 
                            for (Integer pageValue : pageMap.values()) 
                            { 
                                if (pageValue < lruValue){ 
                                    lruValue = pageValue; 
                                    lruPage = pageValue; 
                                } 
                            } 
                            // Remove the page from the linked hash map 
                            pageMap.remove(lruPage); 
                        } 
        
                        // Add the page to the linked hash map 
                        pageMap.put(page, i); 
        
                        // Increment page fault count 
                        pageFaultCount++; 
                        clock ++; 
                    } 
                }
                //Print output
                try{
                    backStore.seek(pgNum * 256);   
                    PrintWriter file = new PrintWriter("correct.txt");
                    
                    for (int i=0; i<=1000; i++){
                        file.println("Virtual address: " + virAddr + "\tPhysical address: " + phyAddr +  "\tValue: " + backStore.readByte()); 
                    }
                    file.close();
                }
                catch (IOException ex) {Logger.getLogger(VirtualMemoryManager.class.getName()).log(SEVERE, null, ex);}
                clock ++; 
            } 
            // Close the buffered reader 
            reader.close(); 
        } 
        catch (IOException e){ 
            e.printStackTrace(); 
        } 

        //Returning statistics
        int stats[] = {clock, tlbHit, pageFaultCount, writeBackCount};
        return stats;
    } 

}