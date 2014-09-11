package edu.miamioh.cse283.lab2;

import java.io.*;
import java.net.*;

/**
 * Template client for CSE283 Lab2, FS2014.
 * 
 * This client should read the following from the command line: 1) the remote
 * address for the server 2) the number of packets that should be requested from
 * the server 3) the size of those packets 4) the sending rate of those packets
 * 
 * @author dk
 */
public class Lab2Client {
	/** Port on which the server will be listening. */
	public static final int PORT = 4242;

	/**
	 * Runs the Lab2Client.
	 * 
	 * @param args
	 *            is an array containing each of the command-line arguments.
	 * @throws IOException
	 *             if there is a networking error.
	 */
	public static void main(String[] args) throws IOException {
		if (args.length != 4) {
			System.out
					.println("Usage: java Lab1Client <inet address> <number> <size in bytes> <rate>");
			return;
		}

		// Construct a socket to use for communication (see: DatagramSocket):
		DatagramSocket s = new DatagramSocket();
		try {
			// assemble the first packet to communicate the packet stream
			// parameters to the server:
			byte[] packData = new byte[5];
			// packing data into a byte array
			ByteArrayOutputStream b = new ByteArrayOutputStream();
			DataOutputStream d = new DataOutputStream(b);
			d.writeByte(Byte.parseByte(args[3]));
			d.writeShort(Short.parseShort(args[1]));
			d.writeShort(Short.parseShort(args[2]));
			packData = b.toByteArray();

			// construct the packet using ip address from args[0] and port 4242
			InetAddress i = InetAddress.getByName(args[0]);
			DatagramPacket tpack = new DatagramPacket(packData,
					packData.length, i, PORT);

			// send the packet
			s.send(tpack);

			// receive a bunch of packets from the server:
			int bytesReceived = 0;
			int loops = 0;
			int size = Integer.parseInt(args[2]);
			DatagramPacket rpack = new DatagramPacket(new byte[size], size);
			// start time on receiving the packets
			long time = System.nanoTime();
			s.setSoTimeout(2000);
			try {
				while (true) {
					s.receive(rpack);
					bytesReceived += rpack.getLength();
					loops++;
				}
			} catch (SocketTimeoutException e) {
			}
			// finished receiving; end time
			double endTime = System.nanoTime() - 2000000000;
			double timeSec = endTime - time;
			// convert to seconds
			timeSec /= 1000000000;
			// calculate bytes/second (see System.currentTimeMillis() or
			// System.nanoTime())
			System.out.println(timeSec + " seconds elapsed."); 
			double throughput = (bytesReceived / timeSec);
			System.out.println("Measured throughput is: " + throughput
					+ " bytes/second");

			// calculate packet loss:
			double packetLoss = ((Integer.parseInt(args[1]) - loops) / timeSec);
			System.out.println("Packet loss averages: " + packetLoss
					+ " packets/second");

		} finally {
			// close the socket:
			if (s != null) {
				s.close();
			}
		}
	}
}
