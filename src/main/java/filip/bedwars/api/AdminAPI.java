package filip.bedwars.api;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import filip.bedwars.api.requests.AdminAPIExecuteActionRequest;
import filip.bedwars.api.requests.AdminAPIGetArenaInfoRequest;
import filip.bedwars.api.requests.AdminAPIGetGameInfoRequest;
import filip.bedwars.api.requests.AdminAPIInitNextGameStateRequest;
import filip.bedwars.api.requests.AdminAPIListArenasRequest;
import filip.bedwars.api.requests.AdminAPIListGamesRequest;
import filip.bedwars.api.requests.AdminAPISkipLobbyCountdownRequest;
import filip.bedwars.api.requests.IAdminAPIRequest;
import filip.bedwars.config.MainConfig;
import filip.bedwars.utils.MessageSender;

public class AdminAPI implements Runnable {
	private final InetSocketAddress listenAddress;
	private boolean listening = false;
	private Thread thread;

	public AdminAPI() {
		listenAddress = MainConfig.getInstance().getAdminApi();

		if (listenAddress == null)
			throw new NullPointerException("listenAddress must not be null");
	}

	public AdminAPI(@NotNull final InetSocketAddress listenAddress) {
		this.listenAddress = listenAddress;
	}

	public void start() {
		if (!listening) {
			listening = true;
			thread = new Thread(this);
			thread.start();
		}
	}

	public void stop() {
		if (listening) {
			thread.interrupt();
			listening = false;
		}
	}

	@Override
	public void run() {
		try (ServerSocket serverSocket = new ServerSocket(listenAddress.getPort(), 10, listenAddress.getAddress())) {
			while (listening) {
				Socket clientSocket = serverSocket.accept();
				try {
					handleClient(clientSocket);
				} catch (IOException e) {}
			}
		} catch (IOException e) {
			if (listening)
				e.printStackTrace();
		}
	}

	// echo -ne "\x00" | timeout 1s nc 127.0.0.1 18080 | hexdump -C
	private void handleClient(Socket clientSocket) throws IOException {
		MessageSender.sendMessage(Bukkit.getConsoleSender(), "New client connected to Admin-API: " + clientSocket.getRemoteSocketAddress());
		DataInputStream in = new DataInputStream(clientSocket.getInputStream());
		DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream());

		int receivedByte;
		while ((receivedByte = in.read()) != -1) {
			IAdminAPIRequest request = null;
			switch (receivedByte) {
				case 0:
					request = new AdminAPIListGamesRequest();
					break;
				case 1:
					request = new AdminAPIGetGameInfoRequest();
					break;
				case 2:
					request = new AdminAPIGetArenaInfoRequest();
					break;
				case 3:
					request = new AdminAPISkipLobbyCountdownRequest();
					break;
				case 4:
					request = new AdminAPIInitNextGameStateRequest();
					break;
				case 5:
					request = new AdminAPIExecuteActionRequest();
					break;
				case 6:
					request = new AdminAPIListArenasRequest();
					break;
			}

			if (request == null) {
				MessageSender.sendMessage(Bukkit.getConsoleSender(), "Admin-API received unknown request from client: " + clientSocket.getRemoteSocketAddress());
				break;
			}

			request.parse(in);
			request.process(out);
			out.flush();
		}

		MessageSender.sendMessage(Bukkit.getConsoleSender(), "Client disconnected from Admin-API: " + clientSocket.getRemoteSocketAddress());
	}
}
