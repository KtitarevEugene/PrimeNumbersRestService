package web_app.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import web_app.common.Constants;
import web_app.common.Utils;
import web_app.messaging.JMSMessage;
import web_app.messaging.JMSProducer;
import web_app.messaging.JMSSession;
import web_app.repository.db.db_models.ResultModel;
import web_app.services.exceptions.BadRequestException;
import web_app.services.exceptions.InternalServerException;
import web_app.services.exceptions.NotFoundException;
import web_app.services.models.ResultResponseModel;
import web_app.services.models.SendResponseModel;
import web_app.services.models.ValueModel;
import web_app.services.models.ValueResultModel;

import javax.jms.JMSException;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/numbers/")
public class PrimeNumbersService extends BaseService {

    private final Logger logger = LoggerFactory.getLogger(PrimeNumbersService.class);

    @GET
    @Path("/{valueId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getResult(@PathParam("valueId") String valueId) throws NotFoundException, BadRequestException {
        if (!Utils.isInteger(valueId)) {
            throw new BadRequestException(new ResultResponseModel(Response.Status.BAD_REQUEST.getStatusCode(),
                    Response.Status.BAD_REQUEST.getReasonPhrase()));
        }

        ResultModel model = getResultModelById(Integer.parseInt(valueId));

        if (model == null) {
            throw new NotFoundException(new ResultResponseModel(Response.Status.NOT_FOUND.getStatusCode(),
                    Response.Status.NOT_FOUND.getReasonPhrase()));
        }

        return Response.ok().entity(
                new ResultResponseModel(
                        Response.Status.OK.getStatusCode(),
                        Response.Status.OK.getReasonPhrase(),
                        new ValueResultModel(
                                model.getId(),
                                Integer.parseInt(model.getValue()),
                                model.getPrimeNumbers())))
                .build();
    }

    private ResultModel getResultModelById(int id) {
        return dataRepository.getResultById(id);
    }

    @PUT
    @Path("/send/{value}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response sendNumber(@PathParam("value") String value) throws BadRequestException, InternalServerException {
        if (!Utils.isInteger(value)) {
            throw new BadRequestException(new SendResponseModel(Response.Status.BAD_REQUEST.getStatusCode(),
                    Response.Status.BAD_REQUEST.getReasonPhrase()));
        }

        List<ResultModel> resultModels = getResultByValue(value);

        try {
            if (resultModels != null && !resultModels.isEmpty()) {
                List<Integer> primeNumbers = resultModels.get(0).getPrimeNumbers();
                if (primeNumbers == null) {
                    sendResultMessage(resultModels.get(0).getId());
                }

                return Response.ok().entity(
                        new SendResponseModel(
                                Response.Status.OK.getStatusCode(),
                                Response.Status.OK.getReasonPhrase(),
                                new ValueModel(
                                        resultModels.get(0).getId(),
                                        Integer.parseInt(value))))
                        .build();

            }

            int valueId = insertValueToSearch(value);

            sendResultMessage(valueId);

            return Response.status(Response.Status.ACCEPTED).entity(
                    new SendResponseModel(
                            Response.Status.ACCEPTED.getStatusCode(),
                            Response.Status.ACCEPTED.getReasonPhrase(),
                            new ValueModel(
                                    valueId,
                                    Integer.parseInt(value))))
                    .build();

        } catch (Exception e) {
            logger.error("Exception has been thrown.", e);
            throw new InternalServerException(new SendResponseModel(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(),
                    e.getMessage()));
        }
    }

    private String sendResultMessage(int valueId) throws JMSException {
        try (JMSSession session = new JMSSession(connection)) {
            try (JMSProducer producer = new JMSProducer(session,
                    messagingProperties.getProperty(Constants.ACTIVE_MQ_QUEUE_NAME))) {
                JMSMessage message = session.createMessage(String.valueOf(valueId));

                return producer.send(message);
            }
        }
    }

    private List<ResultModel> getResultByValue (String value) {
        return dataRepository.getResultByValue(value);
    }

    private int insertValueToSearch(String value) throws Exception {
        return dataRepository.insertRequestedValue(value);
    }
}
