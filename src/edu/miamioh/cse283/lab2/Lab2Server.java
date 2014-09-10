package edu.miamioh.cse283.lab2;

import java.io.*;
import java.net.*;

/**
 * Template server for CSE283 Lab2, FS2014.
 * 
 * This server should respond to a client with a sequence of packets sent at a
 * rate and size determined by the client.
 * 
 * @author dk
 */
public class Lab2Server {
	public static final int PORT = 4242;

	public static void main(String[] args) throws IOException,
			InterruptedException {
		DatagramSocket s = null;

		try {
			// construct a datagram socket listening on port PORT:
			s = new DatagramSocket(PORT);
			//
			System.out.printf("Lab2Server listening on %s:%s \n",
					InetAddress.getLocalHost(), s.getLocalPort());
			while (true) {
				// receive a datagram packet that tells the server how many
				// packets to send, their size in bytes, and their rate:
				byte[] b = new byte[5];
				DatagramPacket pack = new DatagramPacket(b, 5);
				s.receive(pack);
				// get address from the packet to get client's address and port
				InetAddress clientAdd = pack.getAddress();
				int clientPort = pack.getPort();
				// for each packet you're supposed to send:
				ByteArrayInputStream input = new ByteArrayInputStream(
						pack.getData());
				DataInputStream in = new DataInputStream(input);
				// unpacking data from packet
				byte rate = in.readByte();
				short numPack = in.readShort();
				short size = in.readShort();
				// - assemble the packet

				for (int i = 0; i < numPack; i++) {
					DatagramPacket sendPack = new DatagramPacket(
							new byte[size], size, clientAdd, clientPort);
					// - wait the right amount of time to hit the requested
					// sending rate
					Thread.sleep(rate);
					// - send the packet
					s.send(sendPack);
				}
				byte[] end = { -1 };
				for (int i = 0; i < 10; i++)
					s.send(new DatagramPacket(end, 1, clientAdd, clientPort));

			}
		} catch (SocketException ex) { // this will not compile until you start
										// filling in the socket code
			System.out
					.println("Could not open socket (is the server already running?).");
		} finally {
			if (s != null) {
				s.close();
			}
		}
	}
}
