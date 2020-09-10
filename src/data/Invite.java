/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data;

import java.io.Serializable;

/**
 *
 * @author ADMIN
 */
public class Invite implements Serializable {

    private final String opponentName;
    private final int size;

    public Invite(String opponentName, int size) {
        this.opponentName = opponentName;
        this.size = size;
    }

    public String getOpponentName() {
        return opponentName;
    }

    public int getSize() {
        return size;
    }
}
