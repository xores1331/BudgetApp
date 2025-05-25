package pk.edu.pl.PASiR_Sosin_Jakub.controllers;

import jakarta.validation.Valid;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import pk.edu.pl.PASiR_Sosin_Jakub.dto.GroupDTO;
import pk.edu.pl.PASiR_Sosin_Jakub.model.Group;
import pk.edu.pl.PASiR_Sosin_Jakub.service.GroupService;

import java.util.List;

@Controller
public class GroupGraphQLController {
    private final GroupService groupService;

    public GroupGraphQLController(GroupService groupService) {
        this.groupService = groupService;
    }

    @QueryMapping
    public List<Group> groups(){
        return groupService.getAllGroups();
    }

    @MutationMapping
    public Group createGroup(@Valid @Argument GroupDTO groupDTO) {
        return groupService.createGroup(groupDTO);
    }

    @MutationMapping
    public Boolean deleteGroup(@Argument Long id) {
        groupService.deleteGroup(id);
        return true;
    }
}

