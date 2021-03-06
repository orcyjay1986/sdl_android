package com.smartdevicelink.streaming;

import java.io.IOException;
import java.io.InputStream;

import com.smartdevicelink.protocol.ProtocolMessage;
import com.smartdevicelink.protocol.enums.SessionType;

public class StreamPacketizer extends AbstractPacketizer implements Runnable{

	public final static String TAG = "StreamPacketizer";

	private Thread t = null;

	public StreamPacketizer(IStreamListener streamListener, InputStream is, SessionType sType, byte rpcSessionID) throws IOException {
		super(streamListener, is, sType, rpcSessionID);
	}

	public void start() throws IOException {
		if (t == null) {
			t = new Thread(this);
			t.start();
		}
	}

	public void stop() {
		try {
			is.close();
		} catch (IOException ignore) {}
		t.interrupt();
		t = null;
	}

	public void run() {
		int length;

		try {
			while (!Thread.interrupted()) {
				length = is.read(buffer, 0, 1488);
				
				if (length >= 0) {
					ProtocolMessage pm = new ProtocolMessage();
					pm.setSessionID(_rpcSessionID);
					pm.setSessionType(_session);
					pm.setFunctionID(0);
					pm.setCorrID(0);
					pm.setData(buffer, length);
					
			        _streamListener.sendStreamPacket(pm);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
