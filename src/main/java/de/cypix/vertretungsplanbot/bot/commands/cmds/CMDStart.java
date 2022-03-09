package de.cypix.vertretungsplanbot.bot.commands.cmds;

import com.pengrad.telegrambot.model.*;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.model.request.LabeledPrice;
import com.pengrad.telegrambot.model.request.ShippingOption;
import com.pengrad.telegrambot.request.AnswerShippingQuery;
import com.pengrad.telegrambot.request.GetUpdates;
import com.pengrad.telegrambot.request.SendInvoice;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.BaseResponse;
import com.pengrad.telegrambot.response.GetUpdatesResponse;
import com.pengrad.telegrambot.response.SendResponse;
import de.cypix.vertretungsplanbot.bot.commands.TelegramCommand;
import de.cypix.vertretungsplanbot.main.VertretungsPlanBot;
import de.cypix.vertretungsplanbot.sql.SQLManager;
import org.apache.log4j.Logger;

import java.util.Collections;
import java.util.List;

public class CMDStart implements TelegramCommand {

    private static final Logger logger = Logger.getLogger(CMDStart.class);

    @Override
    public void performCommand(User user, Chat chat, Message message, String[] args) {
        if(!SQLManager.isRegistered(chat.id())){
            VertretungsPlanBot.getBot().execute(new SendMessage(chat.id(), "Erfolgreich registriert!"));
            VertretungsPlanBot.getBot().execute(new SendMessage(chat.id(), "Wenn du dich für Vertretungen registrieren möchtest, tippe: /notify"));
            VertretungsPlanBot.getBot().execute(new SendMessage(chat.id(), "Achtung, zusätzliche Stunden werden nicht angezeigt!"));
            SQLManager.insertNewUser(chat.id(), user.firstName(), user.lastName());
            logger.info("New user registered. [name='"+user.lastName()+"', firstname='"+user.firstName()+"', chatId="+chat.id()+"]");
        }else{
            VertretungsPlanBot.getBot().execute(new SendMessage(chat.id(), "Du bist bereits registriert!\n" +
                    "Wenn du hilfe brauchst, benutzte /help"));
        }

        /*Test later....
        SendInvoice sendInvoice = new SendInvoice(chat.id(), "title", "desc", "my_payload",
                "284685063:TEST:MmUzODUxODNiZGFj", "", "EUR", new LabeledPrice("label", 200))
                .needPhoneNumber(true)
                .needShippingAddress(false)
                .isFlexible(true)
                .replyMarkup(new InlineKeyboardMarkup(new InlineKeyboardButton("just pay").pay(),
                        new InlineKeyboardButton("google it").url("www.google.com")));
        SendResponse response = VertretungsPlanBot.getBot().execute(sendInvoice);
        answerShippingQuery();

         */

    }
    public void answerShippingQuery() {
        /*ShippingQuery shippingQuery = getLastShippingQuery();
        String shippingQueryId = shippingQuery != null ? shippingQuery.id() : "invalid_query_id";
        BaseResponse response = VertretungsPlanBot.getBot().execute(new AnswerShippingQuery(shippingQueryId,
                new ShippingOption("1", "VNPT", new LabeledPrice("delivery", 100), new LabeledPrice("tips", 50)),
                new ShippingOption("2", "FREE", new LabeledPrice("free delivery", 0))
        ));
        System.out.println("Description: "+response.description());

        if (!response.isOk()) {
            System.out.println("ERROR CODE: "+response.errorCode());
            System.out.println("Description: "+response.description());
            //assertEquals(400, response.errorCode());
            //assertEquals("Bad Request: QUERY_ID_INVALID", response.description());
        }
        */

    }

    private ShippingQuery getLastShippingQuery() {
        GetUpdatesResponse updatesResponse = VertretungsPlanBot.getBot().execute(new GetUpdates());
        List<Update> updates = updatesResponse.updates();
        Collections.reverse(updates);
        for (Update update : updates) {
            if (update.shippingQuery() != null) {
                return update.shippingQuery();
            }
        }
        return null;
    }
}
