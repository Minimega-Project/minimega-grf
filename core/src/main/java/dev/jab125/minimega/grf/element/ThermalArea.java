/*
 * Copyright (C) 2026 Jab125. All rights reserved unless explicitly stated.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 * --------------------------------------------------------------------
 *
 * ADDITIONAL PERMISSION — CONDITIONAL MINECRAFT LINKING
 *
 * As a special additional permission under section 7 of the GNU General
 * Public License, this program may be used, combined, or distributed as
 * part of a larger work that includes the "minimega-grf:minecraft"
 * subproject, provided that the larger work is distributed under the
 * terms of the GNU General Public License version 3 or later together
 * with the "Minecraft Linking Exception" granted by that subproject.
 *
 * This additional permission applies only when this program is used as
 * part of the Minecraft subproject described above. When used, combined,
 * or distributed independently of that subproject, this program is
 * licensed solely under the GNU General Public License, version 3 or
 * later, without any additional exceptions.
 */
package dev.jab125.minimega.grf.element;

import org.w3c.dom.Document;

import java.util.List;

import static dev.jab125.minimega.grf.element.SpeedDirection.EAST;
import static dev.jab125.minimega.grf.element.SpeedDirection.NATURAL;
import static dev.jab125.minimega.grf.element.SpeedDirection.NORTH;
import static dev.jab125.minimega.grf.element.SpeedDirection.OMNI_EAST;
import static dev.jab125.minimega.grf.element.SpeedDirection.OMNI_NORTH;
import static dev.jab125.minimega.grf.element.SpeedDirection.OMNI_SOUTH;
import static dev.jab125.minimega.grf.element.SpeedDirection.OMNI_WEST;
import static dev.jab125.minimega.grf.element.SpeedDirection.SOUTH;
import static dev.jab125.minimega.grf.element.SpeedDirection.STATIC;
import static dev.jab125.minimega.grf.element.SpeedDirection.WEST;

public final class ThermalArea extends Element {
	public final String name;
	// double liftForceModifier, double speedBoost, double staticLift, double targetHeight,
	//						  SpeedDirection speedDirection
	public final double liftForceModifier, speedBoost, staticLift, targetHeight;
	public final SpeedDirection boostDirection;
	public final double x0, x1, y0, y1, z0, z1;

	public ThermalArea(String name, double liftForceModifier, double speedBoost, double staticLift, double targetHeight, SpeedDirection boostDirection, double x0, double x1, double y0, double y1, double z0, double z1, List<Element> children) {
		super(children);
		this.name = name;
		this.liftForceModifier = liftForceModifier;
		this.speedBoost = speedBoost;
		this.staticLift = staticLift;
		this.targetHeight = targetHeight;
		this.boostDirection = boostDirection;
		this.x0 = x0;
		this.x1 = x1;
		this.y0 = y0;
		this.y1 = y1;
		this.z0 = z0;
		this.z1 = z1;
	}

	public ThermalArea(String name, String liftForceModifier, String speedBoost, String staticLift, String targetHeight, String boostDirection, String x0, String x1, String y0, String y1, String z0, String z1, List<Element> children) {
		this(name, parse2(liftForceModifier), parse(speedBoost), parse(staticLift), parse(targetHeight), decode(boostDirection), Double.parseDouble(x0), Double.parseDouble(x1), Double.parseDouble(y0), Double.parseDouble(y1), Double.parseDouble(z0), Double.parseDouble(z1), children);
	}

	private static double parse(String string) {
		return string.isBlank() ? 0 : Double.parseDouble(string);
	}

	private static double parse2(String string) {
		return string.isBlank() ? 1 : Double.parseDouble(string);
	}

	private static SpeedDirection decode(String boostDirection) {
		return switch (boostDirection) {
			case "plus_x" -> EAST;
			case "minus_x" -> WEST;
			case "plus_z" -> SOUTH;
			case "minus_z" -> NORTH;
			case "omni_plus_x" -> OMNI_EAST;
			case "omni_minus_x" -> OMNI_WEST;
			case "omni_plus_z" -> OMNI_SOUTH;
			case "omni_minus_z" -> OMNI_NORTH;
			case "_static" -> STATIC;
			case "_natural" -> NATURAL;
			default -> throw new IllegalStateException("Unexpected value: " + boostDirection);
		};
	}

	private static String undecode(SpeedDirection boostDirection) {
		return switch (boostDirection) {
			case EAST -> "plus_x";
			case WEST -> "minus_x";
			case SOUTH -> "plus_z";
			case NORTH -> "minus_z";
			case OMNI_EAST -> "omni_plus_x";
			case OMNI_WEST -> "omni_minus_x";
			case OMNI_SOUTH -> "omni_plus_z";
			case OMNI_NORTH -> "omni_minus_z";
			case STATIC -> "_static";
			case NATURAL -> "_natural";
		};
	}

	@Override
	public String getId() {
		return "ThermalArea";
	}

	static class Type implements ElementType<ThermalArea> {
		@Override
		public ThermalArea parseSelf(org.w3c.dom.Element element, List<Element> children) {
			String speedDirection;
			if (element.hasAttribute("staticLift")) {
				speedDirection = "_static";
			} else if (element.hasAttribute("liftForceModifier")) {
				speedDirection = "_natural";
			} else if (element.hasAttribute("speedBoost")) {
				speedDirection = element.getAttribute("boostDirection");
			} else {
				throw new IllegalArgumentException("WHATTTT");
			}

			return new ThermalArea(element.getAttribute("name"), element.getAttribute("liftForceModifier"), element.getAttribute("speedBoost"), element.getAttribute("staticLift"), element.getAttribute("targetHeight"), speedDirection, element.getAttribute("x0"), element.getAttribute("x1"), element.getAttribute("y0"), element.getAttribute("y1"), element.getAttribute("z0"), element.getAttribute("z1"), children);
		}

		@Override
		public org.w3c.dom.Element serializeSelf(
				Document document,
				ThermalArea self,
				List<org.w3c.dom.Element> children
		) {
			org.w3c.dom.Element element = document.createElement(self.getId());

			element.setAttribute("name", self.name);

			if (self.liftForceModifier != 1)
				element.setAttribute("liftForceModifier", Double.toString(self.liftForceModifier));
			if (self.speedBoost != 0) {
				element.setAttribute("speedBoost", Double.toString(self.speedBoost));
				element.setAttribute("boostDirection", undecode(self.boostDirection));
			}

			if (self.staticLift != 0) element.setAttribute("staticLift", Double.toString(self.staticLift));
			if (self.targetHeight != 0) element.setAttribute("targetHeight", Double.toString(self.staticLift));

			element.setAttribute("x0", Double.toString(self.x0));
			element.setAttribute("x1", Double.toString(self.x1));
			element.setAttribute("y0", Double.toString(self.y0));
			element.setAttribute("y1", Double.toString(self.y1));
			element.setAttribute("z0", Double.toString(self.z0));
			element.setAttribute("z1", Double.toString(self.z1));

			children.forEach(element::appendChild);
			return element;
		}
	}
}
