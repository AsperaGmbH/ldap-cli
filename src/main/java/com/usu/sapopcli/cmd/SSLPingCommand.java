package com.usu.sapopcli.cmd;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import org.apache.commons.lang.StringUtils;

public class SSLPingCommand implements CommandRunnable {

	private static final Logger LOGGER = Logger.getLogger(SSLPingCommand.class.getName());

	public static final String EXCLUDED_KEY = "Excluded_Paths";
	public static final String[] EXCLUDED_VALUES = { "target" };

	@Override
	public void run() {
		sslPing();

	}

	private void sslPing() {
		try {
			String hostcomplete = CommandContext.getInstance().nextArgument();
			if (StringUtils.isNotBlank(hostcomplete) && hostcomplete.contains(":")) {
				String hostname = hostcomplete.split(":")[0];
				String port = hostcomplete.split(":")[1];

				SSLSocketFactory sslsocketfactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
				SSLSocket sslsocket = (SSLSocket) sslsocketfactory.createSocket(hostname, Integer.parseInt(port));
				InputStream in = sslsocket.getInputStream();
				OutputStream out = sslsocket.getOutputStream();
				out.write(1);
				while (in.available() > 0) {
					System.out.print(in.read());
				}
				LOGGER.log(Level.INFO, "Successfully connected");
			} else  {
				LOGGER.log(Level.WARNING, "Please use hostname:port_number");
			}
			
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "#### Connection FAILED! ####");
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
		}
	}
}
