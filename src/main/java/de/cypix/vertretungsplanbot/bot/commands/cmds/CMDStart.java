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

import java.util.Collections;
import java.util.List;

public class CMDStart implements TelegramCommand {
    @Override
    public void performCommand(User user, Chat chat, Message message, String[] args) {
        if(!SQLManager.isRegistered(chat.id())){
            VertretungsPlanBot.getBot().execute(new SendMessage(chat.id(), "Hallo!!"));
            VertretungsPlanBot.getBot().execute(new SendMessage(chat.id(), "Wenn du für Vertretungen regestrieren willst Tippe: /notify <KLASSE>\n" +
                    "Zum Beispiel:\n /notify di91"));
            SQLManager.insertNewUser(chat.id(), user.firstName(), user.lastName());
        }else{
            VertretungsPlanBot.getBot().execute(new SendMessage(chat.id(), "Du bist bereits regestriert \n" +
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