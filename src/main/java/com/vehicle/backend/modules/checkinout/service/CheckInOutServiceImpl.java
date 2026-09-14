package com.vehicle.backend.modules.checkinout.service;

import com.vehicle.backend.config.ActiveMQConfig;
import com.vehicle.backend.modules.checkinout.dto.CheckRecordDto;
import com.vehicle.backend.modules.checkinout.entity.CheckRecord;
import com.vehicle.backend.modules.checkinout.entity.CheckType;
import com.vehicle.backend.modules.checkinout.repository.CheckRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CheckInOutServiceImpl implements CheckInOutService {

    private final CheckRecordRepository repository;
    private final JmsTemplate jmsTemplate;

    @Override
    public CheckRecordDto submitCheckRecord(Long bookingId, CheckType type, Integer odometer, Integer fuelLevel, String photoUrls, String notes) {
        String lockbox = (type == CheckType.CHECK_IN) ? String.format("%04d", (int)(Math.random()*10000)) : null;

        CheckRecord record = CheckRecord.builder()
                .bookingId(bookingId)
                .type(type)
                .odometer(odometer)
                .fuelLevelPercentage(fuelLevel)
                .lockboxCode(lockbox)
                .photoUrls(photoUrls)
                .notes(notes)
                .build();

        CheckRecord saved = repository.save(record);

        if (type == CheckType.CHECK_IN) {
            jmsTemplate.convertAndSend(ActiveMQConfig.NOTIFICATION_QUEUE, Map.of(
                    "title", "Check-in Instructions",
                    "message", "Your vehicle lockbox code is: " + lockbox
            ));
        }

        return mapToDto(saved);
    }

    @Override
    public List<CheckRecordDto> getRecordsForBooking(Long bookingId) {
        return repository.findByBookingId(bookingId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    private CheckRecordDto mapToDto(CheckRecord record) {
        return CheckRecordDto.builder()
                .id(record.getId())
                .bookingId(record.getBookingId())
                .type(record.getType())
                .odometer(record.getOdometer())
                .fuelLevelPercentage(record.getFuelLevelPercentage())
                .lockboxCode(record.getLockboxCode())
                .photoUrls(record.getPhotoUrls())
                .notes(record.getNotes())
                .timestamp(record.getTimestamp())
                .build();
    }
}
