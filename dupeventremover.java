 // ===========================  
 // DupEventRemover - License  
 // ===========================  
 //  
 // Copyright (c) 2011, Tobias Zimmer  
 // All rights reserved.  
 //  
 // Redistribution and use in source and binary forms, with or without  
 // modification, are permitted provided that the following conditions are met:  
 //  * Redistributions of source code must retain the above copyright  
 //   notice, this list of conditions and the following disclaimer.  
 //  * Redistributions in binary form must reproduce the above copyright  
 //   notice, this list of conditions and the following disclaimer in the  
 //   documentation and/or other materials provided with the distribution.  
 //  * Neither the name of Tobias Zimmer nor the  
 //   names of any other contributors may be used to endorse or promote products  
 //   derived from this software without specific prior written permission.  
 //  
 // THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND  
 // ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED  
 // WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE  
 // DISCLAIMED. IN NO EVENT SHALL COPYRIGHT HOLDERS AND CONTRIBUTORS BE LIABLE FOR   
 // ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES  
 // (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;  
 // LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND  
 // ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT  
 // (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS  
 // SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.  
   
   
 package de.cwtz.dupeventremover;  
   
 import java.io.FileInputStream;  
 import java.io.FileNotFoundException;  
 import java.io.FileOutputStream;  
 import java.io.IOException;  
 import java.util.Iterator;  
   
 import net.fortuna.ical4j.data.CalendarBuilder;  
 import net.fortuna.ical4j.data.CalendarOutputter;  
 import net.fortuna.ical4j.data.ParserException;  
 import net.fortuna.ical4j.model.Calendar;  
 import net.fortuna.ical4j.model.Component;  
 import net.fortuna.ical4j.model.ComponentList;  
 import net.fortuna.ical4j.model.ValidationException;  
 import net.fortuna.ical4j.model.component.VEvent;  
 import net.fortuna.ical4j.util.CompatibilityHints;  
   
 public class DupEventRemover {  
   
      /**  
       * @param args  
       */  
   
      public static void main(String[] args) {  
           // TODO Auto-generated method stub  
   
           CompatibilityHints.setHintEnabled(  
                     CompatibilityHints.KEY_RELAXED_VALIDATION, true);  
   
           // Reading the file and creating the calendar  
           CalendarBuilder builder = new CalendarBuilder();  
           Calendar cal = null;  
           Calendar[] calOut = null;  
           String inputFile = null;  
           String outputFile = null;  
           String extensionFile = ".ics";  
             
           // Max number of events in one calendar file  
           int googleLimit = 2500;  
             
           //System.out.println(args.length);  
   
           if (args.length > 0) {  
                if (args[0].equals("--help")) {  
                     System.out.println("" +  
                               "Reads an ICal calendar file, removes duplicate event entries and writes the result to one or more new files.\n" +  
                               "\n" +  
                               "DupEventRemover [source | --help] [destination]\n" +  
                               "\n" +  
                               " source \t Specifies the file to read calendar data from. Make sure you give the full name including the file extension.\n" +  
                               " destination \t Specifies the file(s) to write the new calendar to. Omit the file extension. It will be added automatically\n" +  
                               " --help \t Displays this help.\n" +  
                               "\n" +  
                               "If you don't specify 'source' and 'destination', DupEventRemover will by default look for a file\n" +  
                               "named 'my.ics' in it's current directory and write the new calendar to 'my_total_new_x.ics', where x is the number of the file.\n" +  
                               "If you don't specify destination, but only source, the default destination will be used.");  
                     System.exit(0);  
                } else {  
                     inputFile = args[0];  
                }  
           } else {  
                inputFile = "my.ics";  
           }  
   
           if (args.length > 1) {  
                outputFile = args[1];  
           } else {  
                outputFile = "my_total_new_";  
           }  
   
           System.out.println("Reading calendar file...");  
   
           try {  
                cal = builder.build(new FileInputStream(inputFile));  
           } catch (IOException e) {  
                System.out.println(e.getMessage());  
                System.out.println("Try typing 'DupEventRemover --help' for help.");  
                // e.printStackTrace();  
                System.exit(1);  
           } catch (ParserException e) {  
                System.out.println(e.getMessage());  
                // e.printStackTrace();  
                System.exit(1);  
           }  
   
           System.out.println("Start processing. Please wait...");  
   
           int nProcessed = 0;  
           int nDeleted = 0;  
   
           // For each VEVENT in the ICS  
           for (Object o : cal.getComponents("VEVENT")) {  
                Component c = (Component) o;  
                VEvent e = (VEvent) c;  
   
                for (Iterator i = cal.getComponents(Component.VEVENT).iterator(); i  
                          .hasNext();) {  
                     VEvent event = (VEvent) i.next();  
   
                     if ((event.getSummary() != null) && (e.getSummary() != null)  
                               && (event.getStartDate() != null)  
                               && (e.getStartDate() != null)  
                               && (event.getEndDate() != null)  
                               && (e.getEndDate() != null)) {  
                          if ((event.getUid() != e.getUid())  
                                    && (event.getSummary().getValue().equals(e  
                                              .getSummary().getValue()))  
                                    && (event.getStartDate().getValue().equals(e  
                                              .getStartDate().getValue()))  
                                    && (event.getEndDate().getValue().equals(e  
                                              .getEndDate().getValue()))) {  
                               nDeleted++;  
                               cal.getComponents().remove(c);  
                               break;  
                          }  
                     } else {  
                          if (e.getSummary() == null) {  
                               nDeleted++;  
                               cal.getComponents().remove(c);  
                               break;  
                          } else {  
                               // debug:  
                               // System.out.println((VEvent) event);  
                          }  
                     }  
                }  
   
                nProcessed++;  
   
                if ((nProcessed % 100) == 0)  
                     System.out.print(".");  
           }  
           System.out.println("");  
           System.out.println("Number of records processed: " + nProcessed);  
           System.out.println("Number of records deleted: " + nDeleted);  
             
           calOut = new Calendar[(int)Math.floor((nProcessed - nDeleted)/googleLimit)+1];  
           for (int i = 0; i < calOut.length; i++){  
                calOut[i] = new Calendar(cal.getProperties(),new ComponentList());  
           }  
             
           int limitCounter = 0;  
           int calendarCounter = 0;  
           for (Iterator i = cal.getComponents(Component.VEVENT).iterator(); i.hasNext();) {  
                 VEvent event = (VEvent) i.next();  
                   
                 calOut[calendarCounter].getComponents().add(event);  
                 limitCounter++;  
                   
                 if (limitCounter >= googleLimit){  
                      limitCounter = 0;  
                      calendarCounter++;  
                 }        
           }  
             
           // write new calendar file(s)  
           for (int i = 0; i < calOut.length; i++){  
                FileOutputStream fout = null;  
                try {  
                     String outFile = outputFile + i + extensionFile;  
                     fout = new FileOutputStream(outFile);  
                } catch (FileNotFoundException e) {  
                     // TODO Auto-generated catch block  
                     e.printStackTrace();  
                }  
        
                CalendarOutputter outputter = new CalendarOutputter();  
                try {  
                     outputter.output(calOut[i], fout);  
                } catch (IOException e) {  
                     // TODO Auto-generated catch block  
                     e.printStackTrace();  
                } catch (ValidationException e) {  
                     // TODO Auto-generated catch block  
                     e.printStackTrace();  
                }  
           }  
      }  
 }  
