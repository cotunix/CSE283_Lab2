package edu.miamioh.cse283.lab2;

import java.io.*;
import java.net.*;
import java.util.*;

/**
 * Template server for CSE283 Lab2, FS2014.
 * 
 * This server should respond to a client with a sequence of packets 
 * sent at a rate and size determined by the client. 
 * 
 * @author dk
 */
public class Lab2Server {
	public static final int PORT=4242;
	
	public static void main(String[] args) throws IOException, InterruptedException {
		DatagramSocket s=null;

		try {
			// construct a datagram socket listening on port PORT:
			s = new DatagramSocket(PORT);
			// for convenience, the server should tell us what addresses it's listening on;
			// see DatagramSocket.getLocalSocketAddress() and InetAddress.getLocalHost().
			
			// you will probably want to output something like:
			//   "Lab2Server listening on: <ip address>:<port>"			
			System.out.printf("Lab2Server listening on %s:%s \n", InetAddress.getLocalHost(), s.getLocalSocketAddress());
			while(true) {
				// receive a datagram packet that tells the server how many packets to send, their size in bytes, and their rate: 
				byte[] b = new byte[5];
				DatagramPacket pack = new DatagramPacket(b, 5);
				s.receive(pack);
				InetAddress clientAdd = pack.getAddress();
				int clientPort = pack.getPort();
				// for each packet you're supposed to send:
				ByteArrayInputStream input = new ByteArrayInputStream(pack.getData());
				DataInputStream in = new DataInputStream(input);
				byte rate = in.readByte();
				short numPack = in.readShort();
				short size = in.readShort();
				System.out.println(rate);
				System.out.println(numPack);
				System.out.println(size);
				// - assemble the packet
				
				for (int i = 0; i < numPack; i++) {
					DatagramPacket sendPack = new DatagramPacket(new byte[size], size,clientAdd, clientPort);
					// - wait the right amount of time to hit the requested sending rate
					// see: Object.wait(long millis) and the concurrency lesson listed in the lab description
					Thread.sleep(rate);
					// - send the packet
					// end loop
					s.send(sendPack);
					}
				System.out.println("Done sending packets");
				byte[] end = {(byte) 254};
				s.send(new DatagramPacket(end, 1, clientAdd, clientPort));
	
			}
		} catch(SocketException ex) { // this will not compile until you start filling in the socket code
			System.out.println("Could not open socket (is the server already running?).");
		} finally {
			if(s != null) {
				s.close();
			}
		}
	} 
}
