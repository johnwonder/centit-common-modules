package com.centit.support.office.commons;

import com.itextpdf.text.Font;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ExcelUtils {

    public static int getRGB(Color color) {
        int result = 0x00FFFFFF;

        int red = 0;
        int green = 0;
        int blue = 0;

        if (color instanceof HSSFColor) {
            HSSFColor hssfColor = (HSSFColor) color;
            short[] rgb = hssfColor.getTriplet();
            red = rgb[0];
            green = rgb[1];
            blue = rgb[2];
        }

        if (color instanceof XSSFColor) {
            XSSFColor xssfColor = (XSSFColor) color;
            byte[] rgb = xssfColor.getRGB();
            if (rgb != null) {
                red = (rgb[0] < 0) ? (rgb[0] + 256) : rgb[0];
                green = (rgb[1] < 0) ? (rgb[1] + 256) : rgb[1];
                blue = (rgb[2] < 0) ? (rgb[2] + 256) : rgb[2];
            }
        }

        if (red != 0 || green != 0 || blue != 0) {
            result = new java.awt.Color(red, green, blue).getRGB();
        }

        return result;
    }

    /**
     * 获取边框的颜色
     *
     * @param wb    文档对象
     * @param index 颜色版索引
     * @return RGB 三通道颜色
     */
    public static int getBorderRBG(Workbook wb, short index) {
        int result = 0;

        if (wb instanceof HSSFWorkbook) {
            HSSFWorkbook hwb = (HSSFWorkbook) wb;
            HSSFColor color = hwb.getCustomPalette().getColor(index);
            if (color != null) {
                result = getRGB(color);
            }
        }

        if (wb instanceof XSSFWorkbook) {
            XSSFColor color = new XSSFColor();
            color.setIndexed(index);
            result = getRGB(color);
        }

        return result;
    }

    public static CellRangeAddress getColspanRowspanByExcel(Sheet sheet, int rowIndex, int colIndex) {
        CellRangeAddress result = null;
        int num = sheet.getNumMergedRegions();
        for (int i = 0; i < num; i++) {
            CellRangeAddress range = sheet.getMergedRegion(i);
            if (range.getFirstColumn() == colIndex && range.getFirstRow() == rowIndex) {
                result = range;
            }
        }
        return result;
    }

    public static int getPOIColumnWidth(Sheet sheet, Cell cell) {
        int poiCWidth = sheet.getColumnWidth(cell.getColumnIndex());
        int colWidthpoi = poiCWidth;
        int widthPixel = 0;
        if (colWidthpoi >= 416) {
            widthPixel = (int) (((colWidthpoi - 416.0) / 256.0) * 8.0 + 13.0 + 0.5);
        } else {
            widthPixel = (int) (colWidthpoi / 416.0 * 13.0 + 0.5);
        }
        return widthPixel;
    }

    public static  Phrase getPhrase(Workbook wb, Cell cell) {
        //if(this.setting || this.excelObject.getAnchorName() == null){
            return new Phrase(cell.getStringCellValue(), getFontByExcel(wb, cell.getCellStyle()));
        /*}
        Anchor anchor = new Anchor(cell.getStringCellValue() , getFontByExcel(cell.getCellStyle()));
        anchor.setName(this.excelObject.getAnchorName());
        this.setting = true;
        return anchor;*/
    }

    public static PdfPTable toParseContent(Workbook wb, Sheet sheet) throws BadElementException, IOException {
        int rows = sheet.getPhysicalNumberOfRows();

        List<PdfPCell> cells = new ArrayList<PdfPCell>();
        float[] widths = null;
        float mw = 0;
        for (int i = 0; i < rows; i++) {
            Row row = sheet.getRow(i);
            int columns = row.getLastCellNum();

            float[] cws = new float[columns];
            for (int j = 0; j < columns; j++) {
                Cell cell = row.getCell(j);
                if (cell == null) cell = row.createCell(j);

                float cw = getPOIColumnWidth(sheet, cell);
                cws[cell.getColumnIndex()] = cw;

                cell.setCellType(CellType.STRING);
                CellRangeAddress range = getColspanRowspanByExcel(sheet, row.getRowNum(), cell.getColumnIndex());

                int rowspan = 1;
                int colspan = 1;
                if (range != null) {
                    rowspan = range.getLastRow() - range.getFirstRow() + 1;
                    colspan = range.getLastColumn() - range.getFirstColumn() + 1;
                }

                PdfPCell pdfpCell = new PdfPCell();
                pdfpCell.setBackgroundColor(new BaseColor(getRGB(
                    cell.getCellStyle().getFillForegroundColorColor())));
                pdfpCell.setColspan(colspan);
                pdfpCell.setRowspan(rowspan);
                pdfpCell.setVerticalAlignment(getVAlignByExcel(cell.getCellStyle().getVerticalAlignment()));
                pdfpCell.setHorizontalAlignment(getHAlignByExcel(cell.getCellStyle().getAlignment()));
                pdfpCell.setPhrase(getPhrase(wb, cell));

                if (sheet.getDefaultRowHeightInPoints() != row.getHeightInPoints()) {
                    pdfpCell.setFixedHeight(getPixelHeight(row.getHeightInPoints()));
                }

                addBorderByExcel(wb, pdfpCell, cell.getCellStyle());
                addImageByPOICell(sheet, pdfpCell, cell);

                cells.add(pdfpCell);
                j += colspan - 1;
            }

            float rw = 0;
            for (int j = 0; j < cws.length; j++) {
                rw += cws[j];
            }
            if (rw > mw ||  mw == 0) {
                widths = cws;
                mw = rw;
            }
        }

        PdfPTable table = new PdfPTable(widths);
        table.setWidthPercentage(100);
//        table.setLockedWidth(true);
        for (PdfPCell pdfpCell : cells) {
            table.addCell(pdfpCell);
        }
        return table;
    }

    public static float getPixelHeight(float poiHeight){
        float pixel = poiHeight / 28.6f * 26f;
        return pixel;
    }


    public static void addImageByPOICell(Sheet sheet,
                                     PdfPCell pdfpCell , Cell cell) throws BadElementException, IOException{
        if (sheet instanceof HSSFSheet) {
            HSSFSheet hssfSheet = (HSSFSheet) sheet;
            if (hssfSheet.getDrawingPatriarch() != null) {
                List<HSSFShape> shapes = hssfSheet.getDrawingPatriarch().getChildren();
                for (HSSFShape shape : shapes) {
                    HSSFClientAnchor anchor = (HSSFClientAnchor) shape.getAnchor();
                    if (shape instanceof HSSFPicture) {
                        HSSFPicture pic = (HSSFPicture) shape;
                        PictureData data = pic.getPictureData();
                        //String extension = data.suggestFileExtension();
                        int row1 = anchor.getRow1();
                        //int row2 = anchor.getRow2();
                        int col1 = anchor.getCol1();
                        //int col2 = anchor.getCol2();
                        if (row1 == cell.getRowIndex() && col1 == cell.getColumnIndex()) {
                            //Dimension dimension = pic.getImageDimension();
                            //this.anchor = anchor;
                            byte[] bytes = data.getData();
                            if(bytes != null){
                                pdfpCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                                pdfpCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                                Image image = Image.getInstance(bytes);
                                pdfpCell.setImage(image);
                            }
                        }
                    }
                }
            }
        }
    }

    public static int getVAlignByExcel(VerticalAlignment align) {
        int result = 0;
        if (align == VerticalAlignment.BOTTOM) {
            result = Element.ALIGN_BOTTOM;
        }
        if (align == VerticalAlignment.CENTER) {
            result = Element.ALIGN_MIDDLE;
        }
        if (align == VerticalAlignment.JUSTIFY) {
            result = Element.ALIGN_JUSTIFIED;
        }
        if (align == VerticalAlignment.TOP) {
            result = Element.ALIGN_TOP;
        }
        return result;
    }

    public static int getHAlignByExcel(HorizontalAlignment align) {
        int result = 0;
        if (align == HorizontalAlignment.LEFT) {
            result = Element.ALIGN_LEFT;
        }
        if (align == HorizontalAlignment.RIGHT) {
            result = Element.ALIGN_RIGHT;
        }
        if (align == HorizontalAlignment.JUSTIFY) {
            result = Element.ALIGN_JUSTIFIED;
        }
        if (align == HorizontalAlignment.CENTER) {
            result = Element.ALIGN_CENTER;
        }
        return result;
    }


    public static Font getFontByExcel(Workbook wb, CellStyle style) {
        Font result = new Font(PDFPageEvent.BASE_FONT_CHINESE , 8 , Font.NORMAL);
        short index = style.getFontIndex();
        org.apache.poi.ss.usermodel.Font font = wb.getFontAt(index);

        if(font.getBold()){
            result.setStyle(Font.BOLD);
        }

        HSSFColor color = HSSFColor.getIndexHash().get(font.getColor());

        if(color != null){
            int rbg = getRGB(color);
            result.setColor(new BaseColor(rbg));
        }

        FontUnderline underline = FontUnderline.valueOf(font.getUnderline());
        if(underline == FontUnderline.SINGLE){
            String ulString = Font.FontStyle.UNDERLINE.getValue();
            result.setStyle(ulString);
        }
        return result;
    }

    public static void addBorderByExcel(Workbook wb, PdfPCell cell , CellStyle style) {
        cell.setBorderColorLeft(new BaseColor(getBorderRBG(wb,style.getLeftBorderColor())));
        cell.setBorderColorRight(new BaseColor(getBorderRBG(wb,style.getRightBorderColor())));
        cell.setBorderColorTop(new BaseColor(getBorderRBG(wb,style.getTopBorderColor())));
        cell.setBorderColorBottom(new BaseColor(getBorderRBG(wb,style.getBottomBorderColor())));
    }
}
