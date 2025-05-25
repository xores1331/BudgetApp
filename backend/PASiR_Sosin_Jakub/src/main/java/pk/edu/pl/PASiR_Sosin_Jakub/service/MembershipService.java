package pk.edu.pl.PASiR_Sosin_Jakub.service;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import pk.edu.pl.PASiR_Sosin_Jakub.dto.MembershipDTO;
import pk.edu.pl.PASiR_Sosin_Jakub.model.Group;
import pk.edu.pl.PASiR_Sosin_Jakub.model.Membership;
import pk.edu.pl.PASiR_Sosin_Jakub.model.User;
import pk.edu.pl.PASiR_Sosin_Jakub.repository.GroupRepository;
import pk.edu.pl.PASiR_Sosin_Jakub.repository.MembershipRepository;
import pk.edu.pl.PASiR_Sosin_Jakub.repository.UserRepository;

import java.util.List;

@Service
public class MembershipService {
    private final MembershipRepository membershipRepository;
    private final GroupRepository groupRepository;
    private final UserRepository userRepository;

    public MembershipService(MembershipRepository membershipRepository, GroupRepository groupRepository, UserRepository userRepository) {
        this.membershipRepository = membershipRepository;
        this.groupRepository = groupRepository;
        this.userRepository = userRepository;
    }

    public List<Membership> getGroupMembers(Long groupId){
        return membershipRepository.findByGroupId(groupId);
    }

    public Membership addMember(MembershipDTO membershipDTO) {
        User user = userRepository.findByEmail(membershipDTO.getUserEmail()).orElseThrow(
                () -> new EntityNotFoundException("Nie znaleziono uzytkownika o emialu: " + membershipDTO.getUserEmail())
        );

        Group group = groupRepository.findById(membershipDTO.getGroupId()).orElseThrow(
                () -> new EntityNotFoundException("Nie znaleziono grupy o ID: " + membershipDTO.getGroupId())
        );

        boolean alreadyMember = membershipRepository.findByGroupId(group.getId()).stream()
                .anyMatch(membership -> membership.getUser().getId().equals(user.getId()));

        if (alreadyMember) {
            throw new IllegalStateException("Uzytkownik jest juz czlonkiem tej grupy");
        }

        Membership membership = new Membership();
        membership.setUser(user);
        membership.setGroup(group);
        return membershipRepository.save(membership);
    }

    public void removeMember(Long membershipId){
        Membership membership = membershipRepository.findById(membershipId).orElseThrow(
                () -> new EntityNotFoundException("Czlonkostwo nie istnieje")
        );

        User currentUser = getCurrentUser();
        User groupOwner = membership.getGroup().getOwner();

        if(!currentUser.getId().equals(groupOwner.getId())){
            throw new SecurityException("Tylko wlasciciel grupy moze usuwac czlonkow");
        }

        membershipRepository.deleteById(membershipId);
    }

    public User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email).orElseThrow(
                () -> new EntityNotFoundException("Nie znaleziono uzytkownika o email: " + email)
        );
    }
}

