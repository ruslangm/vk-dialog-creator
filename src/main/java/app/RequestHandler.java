package app;

import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.UserActor;
import com.vk.api.sdk.objects.UserAuthResponse;
import com.vk.api.sdk.objects.users.UserXtrCounters;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import utils.Utils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

import static utils.Utils.responseFailed;
import static utils.Utils.responseSuccess;

public class RequestHandler extends AbstractHandler {

    private final int clientId;
    private final VkApiClient vk;
    private String token;
    private int userId;
    private final int GROUP_PEER_ID = 2000000000;

    public RequestHandler(VkApiClient vk, int clientId) {
        this.vk = vk;
        this.clientId = clientId;
    }

    @Override
    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        switch (target) {

            case "/login": {
                try {
                    response.sendRedirect(getOAuthUrl());
                    responseSuccess(response);
                } catch (IOException e) {
                    responseFailed(response, e);
                }
                baseRequest.setHandled(true);
                break;
            }

            case "/add-user": {
                try {
                    this.token = baseRequest.getParameter("token");
                    this.userId = Integer.parseInt(baseRequest.getParameter("user"));
                    responseSuccess(response);
                } catch (NumberFormatException e) {
                    responseFailed(response, e);
                }
                baseRequest.setHandled(true);
                break;
            }

            case "/send-message": {
                try {
                    UserActor actor = new UserActor(userId, token);
                    Integer chatId = vk.messages()
                            .createChat(actor, userId)
                            .title("charity")
                            .execute();
                    int peerId = GROUP_PEER_ID + chatId;
                    String chatLink = vk.messages().getInviteLink(actor).peerId(peerId).execute().getLink();
                    vk.messages().removeChatUser(actor, chatId, String.valueOf(userId)).execute();

                    response.getWriter().println(String.valueOf(chatLink));
                    responseSuccess(response);
                } catch (Exception e) {
                    responseFailed(response, e);
                }

                baseRequest.setHandled(true);
                break;
            }
        }
    }

    private String getOAuthUrl() {
        return "https://oauth.vk.com/authorize?client_id=" + clientId + "&display=page&redirect_uri=" +
                getRedirectUri() + "&scope=messages&response_type=token";
    }

    private String getRedirectUri() {
        return "https://oauth.vk.com/blank.html";
    }
}
