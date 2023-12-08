package Main;

import Enums.Format;

import java.awt.image.BufferedImage;
import java.io.File;

public record Song(File file, Format format, BufferedImage art, String title, String creator, String album, String year, String genre) {
}
