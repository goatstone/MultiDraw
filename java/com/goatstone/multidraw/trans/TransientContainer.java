package com.goatstone.multidraw.trans;

import java.util.Date;

/**
 * Created by goat on 3/23/14.
 */
public class TransientContainer {
    private Date date;
    private Integer c = null;
    public BackgroundProps backgroundProps;
    public TextMessage textMessage;
    public Stroke stroke;

    public TransientContainer() {
    }

    public TransientContainer(BackgroundProps backgroundProps) {
        date = new Date();
        this.backgroundProps = backgroundProps;
    }

    public TransientContainer(BackgroundProps backgroundProps, TextMessage textMessage) {
        date = new Date();
        this.backgroundProps = backgroundProps;
        this.textMessage = textMessage;
    }

    public TransientContainer(TextMessage textMessage) {
        date = new Date();
        this.textMessage = textMessage;
    }
    public TransientContainer(Stroke stroke) {
        date = new Date();
        this.stroke = stroke;
    }
}
