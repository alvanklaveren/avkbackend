package com.alvanklaveren.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.Date;

@Entity
@ToString
@Getter(AccessLevel.PUBLIC)
@Setter(AccessLevel.PUBLIC)
public class Message {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Integer code;

    private String description;
    private String messageText;
    private Date messageDate;

    @Version
    private int version;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="code_category")
    private MessageCategory messageCategory;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="code_forum_user")
    private ForumUser forumUser;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="code_message")
    private Message message; // contains the original message to which THIS message is replying
}
