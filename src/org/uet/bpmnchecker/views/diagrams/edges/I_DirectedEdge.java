/*
 * USE - UML based specification environment
 * Copyright (C) 1999-2004 Mark Richters, University of Bremen
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 2 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */

// $Id: I_DirectedEdge.java 889 2008-04-11 11:52:15Z opti $


package org.uet.bpmnchecker.views.diagrams.edges;

import java.awt.Graphics;

import org.uet.bpmnchecker.views.diagrams.edges.I_DirectedEdge;
import org.uet.bpmnchecker.views.diagrams.util.I_DirectedGraphic;
import org.uet.bpmnchecker.views.diagrams.util.I_DirectedLine;

/**
 * General type of org.tzi.use.gui.views.edges consisting of a head and a tail.
 * Can be used to represent UML standard org.tzi.use.gui.views.edges.
 */
public interface I_DirectedEdge {

    /**
     * Getter for the head of the edge
     *
     * @return head of the edge
     */
    I_DirectedGraphic getHead();

    /**
     * Getter for the tail of the edge
     *
     * @return tail of the edge
     */
    I_DirectedLine getTailLine();

    /**
     * Draws the edge into the given graphic
     *
     * @param graphic graphic to draw into
     * @return the edge
     */
    I_DirectedEdge draw(final Graphics graphic);
}