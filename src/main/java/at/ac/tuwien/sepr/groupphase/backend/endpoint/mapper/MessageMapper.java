package at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.DetailedMessageDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.MessageInquiryDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.SimpleMessageDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.Message;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Named;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Mapper interface for converting between Message entities and DTOs.
 * This mapper provides methods to convert Message entities to different DTO representations
 * and vice versa, facilitating the data transfer between layers of the application.
 */
@Mapper
public interface MessageMapper {

    /**
     * Converts a Message entity to a SimpleMessageDto.
     * This method maps only the basic properties of a message.
     *
     * @param message the Message entity to convert
     * @return the corresponding SimpleMessageDto
     */
    @Named("simpleMessage")
    SimpleMessageDto messageToSimpleMessageDto(Message message);

    /**
     * Converts a list of Message entities to a list of SimpleMessageDtos.
     * This method uses the named mapping "simpleMessage" to handle the conversion
     * since SimpleMessageDto misses the text property.
     *
     * @param message the list of Message entities to convert
     * @return the corresponding list of SimpleMessageDtos
     */
    @IterableMapping(qualifiedByName = "simpleMessage")
    List<SimpleMessageDto> messageToSimpleMessageDto(List<Message> message);

    /**
     * Converts a Message entity to a DetailedMessageDto.
     * This method includes all message properties in the conversion.
     *
     * @param message the Message entity to convert
     * @return the corresponding DetailedMessageDto
     */
    DetailedMessageDto messageToDetailedMessageDto(Message message);

    /**
     * Converts a MessageInquiryDto to a Message entity.
     *
     * @param messageInquiryDto the MessageInquiryDto to convert
     * @return the corresponding Message entity
     */
    Message messageInquiryDtoToMessage(MessageInquiryDto messageInquiryDto);

    /**
     * Converts a date string in "yyyy-MM-dd" format to a Date object.
     *
     * @param dateStr the date string to convert
     * @return the corresponding Date object
     * @throws RuntimeException if the date string is in an invalid format
     */
    default Date stringToDate(String dateStr) {
        try {
            return new SimpleDateFormat("yyyy-MM-dd").parse(dateStr);
        } catch (ParseException e) {
            throw new RuntimeException("Invalid date format: " + dateStr, e);
        }
    }

    /**
     * Converts a Date object to a date string in "yyyy-MM-dd" format.
     *
     * @param date the Date object to convert
     * @return the corresponding date string
     */
    default String dateToString(Date date) {
        return new SimpleDateFormat("yyyy-MM-dd").format(date);
    }


}

