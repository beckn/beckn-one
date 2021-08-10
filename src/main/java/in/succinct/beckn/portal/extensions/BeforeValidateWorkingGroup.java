package in.succinct.beckn.portal.extensions;

import com.venky.core.util.ObjectUtil;
import com.venky.swf.db.Database;
import com.venky.swf.db.extensions.BeforeModelValidateExtension;
import in.succinct.beckn.portal.db.model.collab.Community;
import in.succinct.beckn.portal.db.model.proposal.WorkingGroup;

public class BeforeValidateWorkingGroup extends BeforeModelValidateExtension<WorkingGroup> {
    static {
        registerExtension(new BeforeValidateWorkingGroup());
    }
    @Override
    public void beforeValidate(WorkingGroup group) {
        if (group.getReflector().isVoid(group.getCommunityId())){
            if (ObjectUtil.isVoid(group.getName())){
                throw new RuntimeException("Group must have a name");
            }
            if (group.getReflector().isVoid(group.getGovernedAreaId())){
                throw new RuntimeException("Group must belong to an area");
            }

            Community community = Database.getTable(Community.class).newRecord();
            community.setName(group.getGovernedArea().getName() + "-" + group.getName());
            community.save();
            group.setCommunityId(community.getId());
        }
    }
}
