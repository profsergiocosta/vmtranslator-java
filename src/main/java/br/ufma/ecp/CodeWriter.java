package br.ufma.ecp;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class CodeWriter {

    private StringBuilder output = new StringBuilder();
    private String moduleName = "Main";
    private int synCount = 0;
    private String outputFileName;

    public CodeWriter (String fname) {
        outputFileName = fname;
    }

void setFileName(String s)
{
    moduleName = s.substring(0, s.indexOf("."));
    moduleName = moduleName.substring(s.lastIndexOf("/") + 1);
    System.out.println(moduleName);
}

String registerName(String segment, int index)
{

    if (segment.equals("local"))
        return "LCL";
    if (segment.equals("argument"))
        return "ARG";
    if (segment.equals("this"))
        return "THIS";
    if (segment.equals("that"))
        return "THAT";
    if (segment.equals("pointer"))
        return "R" + (3 + index);
    if (segment.equals("temp"))
        return "R" + (5 + index);

    return moduleName + "." + index; 
}

void writePush(String seg, int index)
{
    if (seg.equals("constant"))
    {
        write("@" + index + " // push " + seg + " " + index);
        write("D=A");
        write("@SP");
        write("A=M");
        write("M=D");
        write("@SP");
        write("M=M+1");
    }
    else if (seg.equals("static") || seg.equals("temp") || seg.equals("pointer"))
    {
        write("@" + registerName(seg, index) + " // push " + seg + " " + index);
        write("D=M");
        write("@SP");
        write("A=M");
        write("M=D");
        write("@SP");
        write("M=M+1");
    }

    else
    {
        write("@" + registerName(seg,0) + " // push " + seg + " " + index);
        write("D=M");
        write("@" + index);
        write("A=D+A");
        write("D=M");
        write("@SP");
        write("A=M");
        write("M=D");
        write("@SP");
        write("M=M+1");
    }
}

void writePop(String seg, int index)
{  
    if (seg.equals("static") || seg.equals("temp") || seg.equals("pointer"))
    {

        write("@SP // pop " + seg + " " + index);
        write("M=M-1");
        write("A=M");
        write("D=M");
        write("@" + registerName(seg, index));
        write("M=D");
    }
    else
    {
        write("@" + registerName(seg,0) + " // pop " + seg + " " + index);
        write("D=M");
        write("@" + index);
        write("D=D+A");
        write("@R13");
        write("M=D");
        write("@SP");
        write("M=M-1");
        write("A=M");
        write("D=M");
        write("@R13");
        write("A=M");
        write("M=D");
    }
}



void writeArithmeticAdd()
{
    write("@SP // add");
    write("M=M-1");
    write("A=M");
    write("D=M");
    write("A=A-1");
    write("M=D+M");
}

void writeArithmeticSub()
{
    write("@SP // sub");
    write("M=M-1");
    write("A=M");
    write("D=M");
    write("A=A-1");
    write("M=M-D");
}

void writeArithmeticNeg()
{
    write("@SP // neg");
    write("A=M");
    write("A=A-1");
    write("M=-M");
}


void writeArithmeticAnd()
{
    write("@SP // and");
    write("AM=M-1");
    write("D=M");
    write("A=A-1");
    write("M=D&M");
}

void writeArithmeticOr()
{
    write("@SP // or");
    write("AM=M-1");
    write("D=M");
    write("A=A-1");
    write("M=D|M");
}

void writeArithmeticNot()
{
    
    write("@SP // not");
    write("A=M");
    write("A=A-1");
    write("M=!M");
}


void writeArithmeticEq()
{
    String label=("JEQ_" + moduleName + "_" + (synCount));
	write("@SP // eq");
	write("AM=M-1");
	write("D=M");
	write("@SP");
	write("AM=M-1");
	write("D=M-D");
	write("@" + label);
	write("D;JEQ");
	write("D=1");
	write("(" + label + ")");
	write("D=D-1");
	write("@SP");
	write("A=M");
	write("M=D");
	write("@SP");
	write("M=M+1");

    synCount++;
}

void writeArithmeticGt()
{
    String labelTrue=("JGT_TRUE_" + moduleName + "_" + (synCount));
    String labelFalse=("JGT_FALSE_" + moduleName + "_" + (synCount));

    write("@SP // gt");
    write("AM=M-1");
    write("D=M");
    write("@SP");
    write("AM=M-1");
    write("D=M-D");
    write("@" + labelTrue);
    write("D;JGT");
    write("D=0");
    write("@" + labelFalse);
    write("0;JMP");
    write("(" + labelTrue + ")");
    write("D=-1");
    write("(" + labelFalse + ")");
    write("@SP");
    write("A=M");
    write("M=D");
    write("@SP");
    write("M=M+1");

    synCount++;
}

void writeArithmeticLt()
{
    String labelTrue = ("JLT_TRUE_" + moduleName + "_" + (synCount)); // toDo ; module
    String labelFalse = ("JLT_FALSE_" + moduleName + "_" + (synCount));

    write("@SP // lt");
    write("AM=M-1");
    write("D=M");
    write("@SP");
    write("AM=M-1");
    write("D=M-D");
    write("@" + labelTrue);
    write("D;JLT");
    write("D=0");
    write("@" + labelFalse);
    write("0;JMP");
    write("(" + labelTrue + ")");
    write("D=-1");
    write("(" + labelFalse + ")");
    write("@SP");
    write("A=M");
    write("M=D");
    write("@SP");
    write("M=M+1");

    synCount++;
}



    private void write (String s) {
        output.append (String.format ("%s\n",s));
    }

    public String codeOutput () {
        return output.toString();
    }

    public  void save() {
  
       
        FileOutputStream outputStream;
        try {
            outputStream = new FileOutputStream(outputFileName);
       
            outputStream.write(output.toString().getBytes());
    
            outputStream.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
