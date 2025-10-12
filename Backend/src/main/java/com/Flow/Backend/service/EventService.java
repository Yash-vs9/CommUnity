package com.Flow.Backend.service;

import com.Flow.Backend.DTO.CreateEvent;
import com.Flow.Backend.DTO.EditEvent;
import com.Flow.Backend.DTO.EventDetailsDTO;
import com.Flow.Backend.exceptions.AccessDeniedException;
import com.Flow.Backend.exceptions.CommunityNotFoundException;
import com.Flow.Backend.exceptions.EventNotFoundException;
import com.Flow.Backend.exceptions.UserNotFoundException;
import com.Flow.Backend.model.CommunityModel;
import com.Flow.Backend.model.EventModel;
import com.Flow.Backend.model.UserModel;
import com.Flow.Backend.repository.CommunityRepository;
import com.Flow.Backend.repository.EventRepository;
import com.Flow.Backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EventService {
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private CommunityRepository communityRepository;
    @Autowired
    private UserRepository userRepository;
    @Transactional
    public String createEvent(CreateEvent createEvent){
        CommunityModel community= communityRepository.findById(createEvent.getCommunityId())
                .orElseThrow(()->new CommunityNotFoundException("Community not found"));
        UserModel user= userRepository.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(()->new UserNotFoundException("user not found"));
        EventModel event = new EventModel();
        event.setHostedBy(createEvent.getHostedBy());
        event.setLocation(createEvent.getLocation());
        event.setDescription(createEvent.getDescription());
        event.setCommunity(community);
        event.setUser(user);
        event.setTitle(createEvent.getTitle());
        event.getJoinedUsers().add(SecurityContextHolder.getContext().getAuthentication().getName());

        eventRepository.save(event);
        return "Event Created successfully ";
    }
    @Transactional
    public String deleteEvent(Long id){
        String username=SecurityContextHolder.getContext().getAuthentication().getName();
        EventModel event=eventRepository.findById(id)
                .orElseThrow(()-> new EventNotFoundException("Event not found"));
        CommunityModel community=event.getCommunity();
        boolean isHosted=event.getHostedBy().equals(username);
        boolean isAdmin=community.getAdmin().contains(username);
        if (!isAdmin&&isHosted){
            throw new AccessDeniedException("Only Admin can delete the Event");
        }
        eventRepository.delete(event);
        return "Event Deleted Successfully by "+username;
    }
    @Transactional
    public String editEvent(EditEvent editEvent){
        String currentusername=SecurityContextHolder.getContext().getAuthentication().getName();
        EventModel event=eventRepository.findById(editEvent.getId())
                .orElseThrow(()->new EventNotFoundException("Event Not found"));
        CommunityModel community= event.getCommunity();
        boolean isHosted=event.getHostedBy().equals(currentusername);
        boolean isAdmin= community.getAdmin().contains(currentusername);
        if(!isAdmin&&isHosted){
            throw new AccessDeniedException("Only admin or hostedby can edit the event details");
        }
        event.setTitle(editEvent.getTitle());
        event.setDescription(editEvent.getDescription());
        event.setLocation(editEvent.getLocation());
        event.setHostedBy(editEvent.getHostedBy());

        eventRepository.save(event);
        return "Event Details changed Successfully by "+ currentusername;
    }
    @Transactional
    public String joinEvent(Long eventId) {
        // Get current logged-in username from Spring Security
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        // Find event by ID
        EventModel event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found with ID: " + eventId));

        // Check if user already joined
        if (event.getJoinedUsers().contains(username)) {
            return "You have already joined this event.";
        }

        // Add user to joined list
        event.getJoinedUsers().add(username);
        eventRepository.save(event);

        return "You have successfully joined the event: " + event.getTitle();
    }
    @Transactional
    public List<EventDetailsDTO> getLatestFiveEvents() {
        List<EventModel> events = eventRepository.findLatestFiveEvents();

        // Map each EventModel to EventDetailsDTO
        return events.stream().map(event -> {
            EventDetailsDTO dto = new EventDetailsDTO();
            dto.setId(event.getId());
            dto.setTitle(event.getTitle());
            dto.setDescription(event.getDescription());
            dto.setHostedBy(event.getHostedBy());
            dto.setLocation(event.getLocation());
            dto.setCreatedAt(event.getCreatedAt());

            if (event.getCommunity() != null) {
                dto.setCommunityId(event.getCommunity().getId());
                dto.setCommunityName(event.getCommunity().getName());
            } else {
                dto.setCommunityId(null);
                dto.setCommunityName("General");
            }

            return dto;
        }).collect(Collectors.toList());
    }
    public List<EventDetailsDTO> getAllEvents() {
        List<EventModel> events = eventRepository.findAllByOrderByCreatedAtDesc();

        return events.stream().map(event -> {
            EventDetailsDTO dto = new EventDetailsDTO();
            dto.setId(event.getId());
            dto.setTitle(event.getTitle());
            dto.setDescription(event.getDescription());
            dto.setHostedBy(event.getHostedBy());
            dto.setLocation(event.getLocation());
            dto.setCreatedAt(event.getCreatedAt());

            if (event.getCommunity() != null) {
                dto.setCommunityId(event.getCommunity().getId());
                dto.setCommunityName(event.getCommunity().getName());
            } else {
                dto.setCommunityId(null);
                dto.setCommunityName("General");
            }

            return dto;
        }).collect(Collectors.toList());
    }
}
