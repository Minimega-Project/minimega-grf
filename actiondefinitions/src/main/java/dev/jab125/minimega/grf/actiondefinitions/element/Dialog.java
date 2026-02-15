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
package dev.jab125.minimega.grf.actiondefinitions.element;

import dev.jab125.minimega.grf.element.Element;
import org.w3c.dom.Document;

import java.util.List;

public final class Dialog extends ACElement implements Effect {

	public final String text;

	public Dialog(String text, List<Element> children) {
		super(children);
		this.text = text;
	}

	@Override
	public String getId() {
		return "Dialog";
	}

	public static class Type implements ACType<Dialog> {
		@Override
		public Dialog parseSelf(org.w3c.dom.Element element, List<Element> children) {
			return new Dialog(unindent(element.getTextContent()).strip(), children);
		}

		@Override
		public org.w3c.dom.Element serializeSelf(
				Document document,
				Dialog self,
				List<org.w3c.dom.Element> children
		) {
			org.w3c.dom.Element element = document.createElement(self.getId());
			element.setTextContent(self.text);
			children.forEach(element::appendChild);

			return element;
		}
	}

	public static String unindent(String str) {
		if (str == null || str.isEmpty()) {
			return str;
		}

		String[] lines = str.split("\\R", -1); // keep trailing empty lines
		int minIndent = Integer.MAX_VALUE;

		// Determine minimum indentation (ignore blank lines)
		for (String line : lines) {
			if (line.trim().isEmpty()) {
				continue;
			}

			int indent = 0;
			while (indent < line.length()) {
				char c = line.charAt(indent);
				if (c == ' ' || c == '\t') {
					indent++;
				} else {
					break;
				}
			}

			minIndent = Math.min(minIndent, indent);
		}

		// If no non-blank lines found
		if (minIndent == Integer.MAX_VALUE) {
			return str;
		}

		// Remove common indentation
		StringBuilder result = new StringBuilder(str.length());
		for (int i = 0; i < lines.length; i++) {
			String line = lines[i];

			if (!line.trim().isEmpty() && line.length() >= minIndent) {
				result.append(line.substring(minIndent));
			} else {
				result.append(line);
			}

			if (i < lines.length - 1) {
				result.append(System.lineSeparator());
			}
		}

		return result.toString();
	}
}
