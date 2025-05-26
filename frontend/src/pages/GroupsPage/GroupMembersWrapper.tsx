import React, { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { groupsApi } from "../../api/groupsApi";
import GroupMembersPage from "./GroupMembersPage";

interface Group {
    id: number;
    name: string;
    ownerId: number;
}

const GroupMembersWrapper: React.FC = () => {
    const { groupId } = useParams<{ groupId: string }>();
    const [group, setGroup] = useState<Group | null>(null);
    const navigate = useNavigate();

    useEffect(() => {
        const fetchGroup = async () => {
            if (groupId) {
                const groupData = await groupsApi.getGroupById(Number(groupId));
                setGroup(groupData);
            }
        };
        fetchGroup();
    }, [groupId]);

    if (!group) return <div>Loading...</div>;

    return (
        <GroupMembersPage
            group={group}
            onBack={() => navigate("/groups")}
        />
    );
};

export default GroupMembersWrapper;
