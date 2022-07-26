package com.alvanklaveren.projections;

import java.util.Date;

public interface MessageListView {

    Integer getCode();
    String getDescription();
    Date getMessageDate();
    Integer getMessageCategoryCode();
    String getForumUserName();
}
