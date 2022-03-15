package bgu.spl.net.impl;

import bgu.spl.net.api.MessageEncoderDecoder;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class MessageEncoderDecoderImpl<T> implements MessageEncoderDecoder {
    private byte[] bytes = new byte[1 << 10]; //start with 1k
    private int len = 0;
    private short opcode = -1;
    private int numberOfArgs = 0;
    private int counter = 0;

    @Override
    public String decodeNextByte(byte nextByte) {
        counter++;
        //System.out.println("Byte number = " + counter);
        //System.out.println("started decoding: " + nextByte);
        if(nextByte == '\0') {
            numberOfArgs++;
            if (opcode != -1)
                pushByte((byte)' ');
            //System.out.println("arguments = " + numberOfArgs);
        }
        //notice that the top 128 ascii characters have the same representation as their utf-8 counterparts
        //this allow us to do the following comparison
        if (nextByte == '\0' & (len > 2 | opcode != -1) &
                ((opcode < 4 & numberOfArgs == 3) |
                        (opcode > 4 & opcode < 11 &numberOfArgs == 2) |
                        ((opcode == 4 | opcode == 11) & len == 1)) ) {
            String toSend = popString();
            bytes = new byte[1 << 10];
            opcode = -1;
            return toSend;
        }
        pushByte(nextByte);
        //System.out.println("current string = " + new String(bytes, 0, len, StandardCharsets.UTF_8));
        //System.out.println("len = " + len);
        if(len == 2 & opcode == -1) {
            opcode = bytesToShort(bytes);
            //System.out.println("short = " + opcode);
            len = 0;
        }
        return null; //not a line yet
    }

    private void pushByte(byte nextByte) {
        if (len >= bytes.length) {
            bytes = Arrays.copyOf(bytes, len * 2);
        }

        bytes[len++] = nextByte;
    }

    private String popString() {
        //notice that we explicitly requesting that the string will be decoded from UTF-8
        //this is not actually required as it is the default encoding in java.
        String result = new String(bytes, 0, len, StandardCharsets.UTF_8);
        len = 0;
        numberOfArgs = 0;
        //System.out.println(opcode + " " + result);
        return opcode + " " + result;
    }
    public short bytesToShort(byte[] byteArr) {
        short result = (short)((byteArr[0] & 0xff) << 8);
        result += (short)(byteArr[1] & 0xff);
        if(result < 1 | result > 11){
            result = (short)((byteArr[1] & 0xff) << 8);
            result += (short)(byteArr[2] & 0xff);
        }
        return result;
    }


    @Override
    public byte[] encode(Object message) {
        String[] args = message.toString().split(" ");
        byte[] bytesArr = new byte[5];
        bytesArr[0] = (byte)((Short.parseShort(args[0]) >> 8) & 0xFF);
        bytesArr[1] = (byte)(Short.parseShort(args[0]) & 0xFF);
        bytesArr[2] = (byte)((Short.parseShort(args[1]) >> 8) & 0xFF);
        bytesArr[3] = (byte)(Short.parseShort(args[1]) & 0xFF);
        if(Integer.parseInt(args[0]) == 12 & ((Integer.parseInt(args[1]) > 5 & Integer.parseInt(args[1]) < 10) | Integer.parseInt(args[1]) == 11)){
            String msg = message.toString().substring(args[0].length()+args[1].length()+2);
            byte[] bytes = (msg + "\0").getBytes();
            byte[] result = new byte[bytesArr.length+bytes.length-1];
            for (int i = 0; i < result.length; i++) {
                if(i < 4)
                    result[i] = bytesArr[i];
                else
                    result[i] = bytes[i-4];
            }
            return result;
        }
        bytesArr[4] = (byte) '\0';
        return bytesArr; //uses utf8 by default
    }
}
