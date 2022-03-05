package com.nurujjamanpollob.njpsmultimedia.loaders;
import java.io.*;
import java.util.*;

public class NJPollobExceptionWriter
{
    String dir;
    String fileName;
    String exception;
    String className;
    boolean isUseAppendLog = true;

    public NJPollobExceptionWriter(String directoryInStringFromat, String fileNamewithExtension, String exceptionDetails){

        this.dir = directoryInStringFromat;
        this.fileName = fileNamewithExtension;
        this.exception = exceptionDetails;

    }

    public String getLogsFromFile(){

        File file = new File(dir+"\n", fileName);
        StringBuilder sb = new StringBuilder();
        if(file.exists()){

            try
            {
                FileReader reader = new FileReader(file);
                BufferedReader bfreader = new BufferedReader(reader);
                String line;

                while((line=bfreader.readLine()) != null){

                    sb.append(line);
                    sb.append("\n");
                }

                bfreader.close();
                return sb.toString();
            }
            catch (Exception e)
            {

                callMeOnGeneralError(e);
            }
        }

        return null;
    }

    public void performWriteOperation(){

        //Start making file by dirs and file name

        File file = new File(dir+"/", fileName);

        if(isUseAppendLog){

            if(file.exists()){

                operateAppendOperation(file);
            }else{

                operateFirstWrite(file);
            }


        }
        if(!isUseAppendLog){

            operateFirstWrite(file);
        }


    }


    public void setClassNameForLog(String className){

        this.className = className;

    }

    public void isIncludeLogToExistingLogFile(Boolean flag){

        this.isUseAppendLog = flag;

    }

    private void operateAppendOperation(File file){

        String currentTimeData = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
        //Start try
        try{

            FileReader reader = new FileReader(file);
            BufferedReader bfreader = new BufferedReader(reader);
            String line;
            StringBuilder textInside = new StringBuilder();

            //start while loop
            while((line= bfreader.readLine()) != null){

                textInside.append(line);
                textInside.append("\n");

            }

            textInside.append("[BEGIN INCLUDE]\n\n");
            if(className != null){
                textInside.append("Class Name:  ").append(className).append("\n");
            }
            textInside.append("Time when recorded:  ").append(currentTimeData).append("\n\n").append("Message:  ").append(exception).append("\n\n").append("[END INCLUDE]\n\n");

            //close reader
            bfreader.close();

            // now we need to write back all exception!
            FileWriter w = new FileWriter(file);
            w.append(textInside);
            w.flush();
            w.close();
        }catch(Exception ess){

            callMeOnGeneralError(ess);

        } // end try

    }

    private void operateFirstWrite(File file){

        try
        {
            String currentTimeData = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
            FileWriter w = new FileWriter(file);
            w.append("[BEGIN INCLUDE]\n\n");
            if(className != null){
                w.append("Class Name:  ").append(className).append("\n");
            }
            w.append("Time when recorded:  ").append(currentTimeData).append("\n\n").append("Message:  ").append(exception).append("\n\n").append("[END INCLUDE]\n\n");
            w.flush();
            w.close();
        }
        catch (IOException jje)
        {

            callMeOnIOError(jje);
        }

    }

    //Set up listener interface

    public interface OnSomethingWrong{

        public void onIOError(IOException IOException);
        public void onGeneralException(Exception Exception);
    }

    private OnSomethingWrong errCls = null;

    private void callMeOnIOError(IOException ioerr){
        if(errCls != null){
            this.errCls.onIOError(ioerr);
        }
    }
    private void callMeOnGeneralError(Exception exc){
        if(errCls != null){
            this.errCls.onGeneralException(exc);
        }
    }

    public void setListnerForError(OnSomethingWrong NJPollobExceptionWriterDotOnSomethingWrong){

        this.errCls = NJPollobExceptionWriterDotOnSomethingWrong;
    }

}
