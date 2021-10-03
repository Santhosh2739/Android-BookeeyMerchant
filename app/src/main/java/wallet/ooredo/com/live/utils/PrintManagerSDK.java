package wallet.ooredo.com.live.utils;

import android.content.Context;
import android.device.PrinterManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;

import java.util.Arrays;
/**
 * @Description：
 * @author luoan
 * @version time：2019-7-23 下午3:26:07
 */
public class PrintManagerSDK {
    public final static String FONT = "font";
    public final static int FONT_SMALL = 0;
    public final static int FONT_NORMAL = 1;
    public final static int FONT_LARGE = 2;
    public final static String ALIGN = "align";
    public final static int ALIGN_LEFT = 0;
    public final static int ALIGN_CENTER = 1;
    public final static int ALIGN_RIGHT = 2;
    public final static String WIDTH = "width";
    public final static String HEIGHT = "height";
    public final static String EXHEIGHT = "expectedHeight";
    public final static String OFFSET = "offset";
    public final static String UNDERLINE = "underline";
    public final static String AUTOTRUNC = "autoTrunc";
    public final static String NEWLINE = "newline";
    public static final int ERROR_NONE = 0x00;
    public static final int ERROR_PAPERENDED = 0xF0;
    public static final int ERROR_HARDERR = 0xF2;
    public static final int ERROR_OVERHEAT = 0xF3;
    public static final int ERROR_BUFOVERFLOW = 0xF5;
    public static final int ERROR_LOWVOL = 0xE1;
    public static final int ERROR_PAPERENDING = 0xF4;
    public static final int ERROR_MOTORERR = 0xFB;
    public static final int ERROR_PENOFOUND = 0xFC;
    public static final int ERROR_PAPERJAM = 0xEE;
    public static final int ERROR_NOBM = 0xF6;
    public static final int ERROR_BUSY = 0xF7;
    public static final int ERROR_BMBLACK = 0xF8;
    public static final int ERROR_WORKON = 0xE6;
    public static final int ERROR_LIFTHEAD = 0xE0;
    public static final int ERROR_CUTPOSITIONERR = 0xE2;
    public static final int ERROR_LOWTEMP = 0xE3;

    private boolean initPage = false;

    private Context mContext;
    private PrinterManager mPrinter;

    private int feedLine = 0;
    int currentYPoint = 0;
    private static final int MAX_PAGEWIDTH = 384;
    private static final int DEF_FONT_SIZE_SMALL = 16;
    private static final int DEF_FONT_SIZE = 24;//2 * DEF_FONT_SIZE_SMALL;
    private static final int DEF_FONT_SIZE_BIG = 32;//4 * DEF_FONT_SIZE_SMALL;

    private static final int MEG_CMD_STARTPRINT = 0X0001;
    private static final int MEG_CMD_STARTPRINT_CACHE = 0X0002;

    private Paint mPaint;
    private int mFontsize = DEF_FONT_SIZE;

    private int mStatus = 0;
    private Context context;
    //font name or ttf path.
    String fontName = "simsun";
           // Environment.getExternalStorageDirectory()+"/shtrixfr.ttf";

    public void initPrint(Context context){

        this.context = context;
        mPrinter = new PrinterManager();
        mPrinter.open();
        currentYPoint = 0;
    }

    public int getStatus(){

        int ret = -1;
        if (mPrinter != null) {
            if (mStatus != ERROR_BUSY) {
                ret = mPrinter.getStatus();
                if (ret == 0) {
                    mStatus = 0;
                } else if (ret == -1) {
                    mStatus = ERROR_PAPERENDED;
                } else if (ret == -2) {
                    mStatus = ERROR_OVERHEAT;
                } else if (ret == -3) {
                    mStatus = ERROR_LOWVOL;
                } else if (ret == -4) {
                    mStatus = ERROR_BUSY;
                } else if (ret == -256) {
                    mStatus = ERROR_MOTORERR;
                } else if (ret == -257) {
                    mStatus = ERROR_HARDERR;
                }
            }
        }
        return mStatus;
    }

    /**
     * 针对物品清单特别处理
     * @param goodsName   物品名称
     * @param goodsNum    物品数量
     * @param goodsAmount 物品总价格
     * @param font 字体大小，0：小，1：中，2：大
     */
    public void addTextGoodsList(String goodsName, String goodsNum, String goodsAmount,  int font){

        if (mPrinter != null) {
            if (!initPage) {
                mPrinter.setupPage(384, -1);
                initPage = true;
            }
        }
        int goodsNameheight = 0;
        int height = currentYPoint;
        int fontsize = DEF_FONT_SIZE;
        if (font == FONT_SMALL) {
            fontsize = DEF_FONT_SIZE_SMALL;
        } else if (font == FONT_NORMAL) {
            fontsize = DEF_FONT_SIZE;
        } else if (font == FONT_LARGE) {
            fontsize = DEF_FONT_SIZE_BIG;
        } else {
            fontsize = DEF_FONT_SIZE;
        }
        int maxLine = 128;
        int fontStyle = 0;
        int xPoint = 0;
        int nowrap = 1;
        int feedLine = 1;
        int feedLineGoosNum = 1;//换行次数
        int feedLineGoosAmount = 1;//换行次数
        float totalWidth = 0;
        String text = "";
        StringBuffer stringBuffer = new StringBuffer();
        StringBuffer stringBuffer2 = new StringBuffer();
        if(!TextUtils.isEmpty(goodsName)){
            text = goodsName;
            goodsNameheight = mPrinter.drawTextEx(text, xPoint, currentYPoint, maxLine, -1, fontName, fontsize, 0,
                    fontStyle, 0)+5;
        }
        stringBuffer.setLength(0);
        stringBuffer2.setLength(0);
        height = currentYPoint;
        if(!TextUtils.isEmpty(goodsNum)){
            text = goodsNum;
            for (int a=0; a<text.length(); a++) {
                stringBuffer.append(text.charAt(a));
                if(Character.isSpaceChar(text.charAt(a))){
                    stringBuffer2.append("=");
                }else{
                    stringBuffer2.append(text.charAt(a));
                }
                Paint mPaint = getPaint(fontsize, false, false);
                float width = mPaint.measureText(stringBuffer.toString());
                if (width >= maxLine) {
                    height += mPrinter.drawTextEx(stringBuffer.toString(), 130, height, maxLine,
                            -1, fontName, fontsize, 0, fontStyle, nowrap);
                    stringBuffer.setLength(0);
                    stringBuffer2.setLength(0);
                    feedLineGoosNum++;
                } else if (a == text.length() - 1) {//最后一排
                    String lastStr = stringBuffer.toString();
                    if (TextUtils.isEmpty(lastStr))
                        return;
                    totalWidth = mPaint.measureText(lastStr);
                    xPoint = (int) (MAX_PAGEWIDTH - totalWidth) / 2;
                    height += mPrinter.drawTextEx(stringBuffer.toString(), xPoint, height, maxLine,
                            -1, fontName, fontsize, 0, fontStyle, nowrap);
                }
            }
        }

        stringBuffer.setLength(0);
        stringBuffer2.setLength(0);
        height = currentYPoint;
        if(!TextUtils.isEmpty(goodsAmount)){
            text = goodsAmount;
            for (int a=0; a<text.length(); a++) {
                stringBuffer.append(text.charAt(a));
                if(Character.isSpaceChar(text.charAt(a))){
                    stringBuffer2.append("=");
                }else{
                    stringBuffer2.append(text.charAt(a));
                }
                Paint mPaint = getPaint(fontsize, false, false);
                float width = mPaint.measureText(stringBuffer.toString());
                if (width >= maxLine) {
                    height += mPrinter.drawTextEx(stringBuffer.toString(), 260, currentYPoint, maxLine,
                            -1, fontName, fontsize, 0, fontStyle, nowrap);
                    stringBuffer.setLength(0);
                    stringBuffer2.setLength(0);
                    feedLineGoosAmount++;
                } else if (a == text.length() - 1) {//最后一排
                    String lastStr = stringBuffer.toString();
                    if (TextUtils.isEmpty(lastStr))
                        return;
                    totalWidth = mPaint.measureText(lastStr);
                    xPoint = (int) (MAX_PAGEWIDTH - totalWidth)-5;
                    height += mPrinter.drawTextEx(stringBuffer.toString(), xPoint, height, maxLine,
                            -1, fontName, fontsize, 0, fontStyle, nowrap);
                }
            }
        }
        //对比两个换行
        feedLine = (feedLineGoosNum>feedLineGoosAmount)?feedLineGoosNum:feedLineGoosAmount;
        if(goodsNameheight>(26*feedLine)+10){
            currentYPoint += goodsNameheight;
        }else{
            currentYPoint += (28*feedLine)+5;
        }
    }

    public void addText(String text, int font, int align) {
        // TODO Auto-generated method stub
        if (text == null) {
            return;
        }
        int fontStyle = 0;
        int nowrap = 1;
        int xPoint = 0;
        int fontsize = DEF_FONT_SIZE;
        boolean isBoldFont = false;
        if (mPrinter != null) {
            if (!initPage) {
                mPrinter.setupPage(384, -1);
                initPage = true;
            }
            if (font == FONT_SMALL) {
                fontsize = DEF_FONT_SIZE_SMALL;
            } else if (font == FONT_NORMAL) {
                fontsize = DEF_FONT_SIZE;
            } else if (font == FONT_LARGE) {
                fontsize = DEF_FONT_SIZE_BIG;
            } else {
                fontsize = DEF_FONT_SIZE;
            }
            if (mPaint == null) {
                mPaint = getPaint(fontsize, isBoldFont, false);
                mFontsize = fontsize;
            } else if (mFontsize != fontsize) {
                mPaint.reset();
                mPaint = getPaint(fontsize, isBoldFont, false);
                mFontsize = fontsize;
            }
            float totalWidth = 0;
            if (currentYPoint == 0) {
                currentYPoint = currentYPoint + 5;
            }

            int maxLine = 384;
            if (font == FONT_SMALL) {
                maxLine = 384;
            } else if (font == FONT_NORMAL) {
                maxLine = 384;
            } else if (font == FONT_LARGE) {
                maxLine = 384 - 15;
            }
            StringBuffer stringBuffer = new StringBuffer();
            StringBuffer stringBuffer2 = new StringBuffer();
            if(ALIGN_LEFT == align){
                currentYPoint += mPrinter.drawTextEx(text, xPoint, currentYPoint, MAX_PAGEWIDTH, -1, fontName, fontsize, 0,
                        fontStyle, 0);
            }else{
                for (int a=0; a<text.length(); a++) {
                    stringBuffer.append(text.charAt(a));
                    if(Character.isSpaceChar(text.charAt(a))){
                        stringBuffer2.append("=");
                    }else{
                        stringBuffer2.append(text.charAt(a));
                    }
                    Paint mPaint = getPaint(fontsize, false, false);
                    float width = mPaint.measureText(stringBuffer.toString());
                    if (width >= maxLine) {
                        currentYPoint += mPrinter.drawTextEx(stringBuffer.toString(), 0, currentYPoint, maxLine,
                                -1, fontName, fontsize, 0, fontStyle, nowrap) + 5;
                        stringBuffer.setLength(0);
                        stringBuffer2.setLength(0);
                    } else if (a == text.length() - 1) {//最后一排
                        String lastStr = stringBuffer.toString();
                        if (TextUtils.isEmpty(lastStr))
                            return;
                        if (ALIGN_CENTER == align) {
                            totalWidth = mPaint.measureText(lastStr);
                            xPoint = (int) (maxLine - totalWidth) / 2;
                        } else if (ALIGN_RIGHT == align) {
                            totalWidth = mPaint.measureText(lastStr);
                            xPoint = (int) (maxLine - totalWidth);
                        }
                        currentYPoint += mPrinter.drawTextEx(stringBuffer.toString(), xPoint, currentYPoint, maxLine,
                                -1, fontName, fontsize, 0, fontStyle, nowrap) + 5;
                    }
                }
            }
        }
    }


    /**
     * 将字符串，并将前面N个字符加粗
     * @param text
     * @param font
     * @param align
     * @param blodFrontSize 前面是否加粗的字符数
     * @param autoWrap 是否自动换行
     * @param isFrontBlod 是否前面加粗  true:前面加粗   false：前面不加粗
     * @throws RemoteException
     */
    public void addTextBlodLenght(String text, int font, int align, int blodFrontSize,boolean autoWrap,boolean isFrontBlod){
        try {
            if (TextUtils.isEmpty(text)) {
                return;
            }
            if (text.length() < blodFrontSize) {
                blodFrontSize = text.length();
            }
            if (blodFrontSize < 0) {
                blodFrontSize = 0;
            }
            int fontStyle = 0;
            int nowrap = 1;
            int xPoint = 0;
            int fontsize = DEF_FONT_SIZE;
            boolean isBoldFont = false;
            if (mPrinter != null) {
                if (!initPage) {
                    mPrinter.setupPage(384, -1);
                    initPage = true;
                }
                if (font == FONT_SMALL) {
                    fontsize = DEF_FONT_SIZE_SMALL;
                } else if (font == FONT_NORMAL) {
                    fontsize = DEF_FONT_SIZE;
                } else if (font == FONT_LARGE) {
                    fontsize = DEF_FONT_SIZE_BIG;
                } else {
                    fontsize = DEF_FONT_SIZE;
                }
                if (mPaint == null) {
                    mPaint = getPaint(fontsize, isBoldFont, false);
                    mFontsize = fontsize;
                } else if (mFontsize != fontsize) {
                    mPaint.reset();
                    mPaint = getPaint(fontsize, isBoldFont, false);
                    mFontsize = fontsize;
                }
                if (currentYPoint == 0) {
                    currentYPoint = currentYPoint + 5;
                }

                float maxLine = 384;
                float totalWidth = 0;
                int chaWidth = (int) mPaint.measureText("中");
                if (autoWrap) { //换行
                    StringBuffer sb = new StringBuffer();
                    StringBuffer stringBuffer2 = new StringBuffer();//用于计算占位宽度的字符串
                    int startPosition = 0;//记录打印改行第一个字符的在打印字符串当中的位置
                    for (int a = 0; a < text.length(); a++) {
                        sb.append(text.charAt(a));
                        stringBuffer2.append(getCharSeat(text.charAt(a)));

                        float width =  mPaint.measureText(stringBuffer2.toString());
                        // 增加判断  要是再取一个字就超过最大宽度了 这时就需要换行
                        float nexWidth = 0;
                        if (a < text.length() - 1) { // 未到最后
//                        char nexChar = text.charAt(a + 1);
                            char nexChar = getCharSeat(text.charAt(a + 1));
                            String nextStr = String.valueOf(nexChar);
                            if (" ".equals(nextStr)) {
                                nextStr = "=";
                            }
                            if (!TextUtils.isEmpty(nextStr)) {
                                nexWidth = mPaint.measureText(nextStr);
                            }
                        }
                        if (width + nexWidth >= maxLine) {// 如果取下一个字回放不下 就需要换行的
                            if (font == FONT_LARGE) {
                                fontStyle |= 0x4000; // 大字体将字体进行拉高
                            }
                            String lastStr = sb.toString();
                            int printHeight = 0;
                            if (blodFrontSize - 1 < startPosition) {//整行不加粗
                                if (isFrontBlod){
                                    fontStyle &= 0xfffE;
                                }else {
                                    fontStyle |= 0x0001;
                                }
                                printHeight = mPrinter.drawTextEx(lastStr, xPoint, currentYPoint, (int) maxLine,
                                        -1, fontName, fontsize, 0, fontStyle, nowrap);
                            } else if (blodFrontSize - 1 > a) {//整行加粗
                                if (isFrontBlod){
                                    fontStyle |= 0x0001;
                                }else {
                                    fontStyle &= 0xfffE;
                                }
                                printHeight = mPrinter.drawTextEx(lastStr, xPoint, currentYPoint, (int) maxLine,
                                        -1, fontName, fontsize, 0, fontStyle, nowrap);
                            } else {//部分加粗，部分不加粗
                                String firstPrint = lastStr.substring(0, blodFrontSize - startPosition);
                                if (isFrontBlod){
                                    fontStyle |= 0x0001;
                                }else {
                                    fontStyle &= 0xfffE;
                                }
                                printHeight = mPrinter.drawTextEx(firstPrint, xPoint, currentYPoint, (int) maxLine,
                                        -1, fontName, fontsize, 0, fontStyle, nowrap);

                                String secondPrint = lastStr.substring(blodFrontSize - startPosition, a - startPosition + 1);
                                if (!TextUtils.isEmpty(secondPrint)) {
                                    //计算第二次打印X轴的坐标（加上第一次打印占位字符的长度）
                                    String distanceStr = stringBuffer2.toString().substring(0, blodFrontSize - startPosition);
                                    xPoint += (mPaint.measureText(distanceStr) - getPointXForkRake(distanceStr,font));
                                    if (isFrontBlod){
                                        fontStyle &= 0xfffE;
                                    }else {
                                        fontStyle |= 0x0001;
                                    }
                                    printHeight = mPrinter.drawTextEx(secondPrint, xPoint, currentYPoint, (int) maxLine,
                                            -1, fontName, fontsize, 0, fontStyle, nowrap);
                                }
                            }
                            currentYPoint += printHeight;

                            xPoint = 0;
                            maxLine = 384;
                            sb.setLength(0);
                            stringBuffer2.setLength(0);
                            startPosition = a + 1;//下一行的打印起始位置
                        } else if (a == text.length() - 1) {//最后一排
                            String lastStr = sb.toString();
                            if (TextUtils.isEmpty(lastStr))
                                return;
                            String tempStr = stringBuffer2.toString();
                            totalWidth = mPaint.measureText(tempStr);
                            if (ALIGN_CENTER == align) {
                                // totalWidth = mPaint.measureText(lastStr);
                                xPoint = (int) (MAX_PAGEWIDTH - totalWidth) / 2;
                            } else if (ALIGN_RIGHT == align) {
                                // totalWidth = mPaint.measureText(lastStr);
                                xPoint = (int) (MAX_PAGEWIDTH - totalWidth);
                            }
                            if (xPoint < 0) {
                                xPoint = 0;
                            }
                            if (font == FONT_NORMAL) {
                                fontStyle |= 0x4000; // 大字体将字体进行拉高
                            }
                            int printHeight = 0;
                            if (blodFrontSize - 1 < startPosition) {//如果打印的起始位置在加粗之后，直接打印
                                if (isFrontBlod){
                                    fontStyle &= 0xfffE;
                                }else {
                                    fontStyle |= 0x0001;
                                }
                                printHeight = mPrinter.drawTextEx(lastStr, xPoint, currentYPoint, (int) maxLine,
                                        -1, fontName, fontsize, 0, fontStyle, nowrap);
                            } else if (blodFrontSize - 1 > a) {//最后一排不会出现这种情况(加粗长度超过打印的字符串长度)
                                if (isFrontBlod){
                                    fontStyle |= 0x0001;
                                }else {
                                    fontStyle &= 0xfffE;
                                }
                                printHeight = mPrinter.drawTextEx(lastStr, xPoint, currentYPoint, (int) maxLine,
                                        -1, fontName, fontsize, 0, fontStyle, nowrap);
                            } else {
                                String firstPrint = lastStr.substring(0, blodFrontSize - startPosition);
                                if (isFrontBlod){
                                    fontStyle |= 0x0001;
                                }else {
                                    fontStyle &= 0xfffE;
                                }
                                printHeight = mPrinter.drawTextEx(firstPrint, xPoint, currentYPoint, (int) maxLine,
                                        -1, fontName, fontsize, 0, fontStyle, nowrap);

                                String secondPrint = lastStr.substring(blodFrontSize - startPosition);
                                if (!TextUtils.isEmpty(secondPrint)) {
                                    //计算第二次打印X轴的坐标（加上第一次打印占位字符的长度）
                                    String distanceStr = stringBuffer2.toString().substring(0, blodFrontSize - startPosition);
                                    xPoint += (mPaint.measureText(distanceStr) - getPointXForkRake(distanceStr,font));
                                    if (isFrontBlod){
                                        fontStyle &= 0xfffE;
                                    }else {
                                        fontStyle |= 0x0001;
                                    }
                                    printHeight = mPrinter.drawTextEx(secondPrint, xPoint, currentYPoint, (int) maxLine,
                                            -1, fontName, fontsize, 0, fontStyle, nowrap);
                                }
                            }
                            currentYPoint += printHeight;
                        }
                    }
                } else {
                    StringBuffer stringBuffer = new StringBuffer();
                    String printText = text;
                    for (int a = 0; a < text.length(); a++) {
                        stringBuffer.append(getCharSeat(text.charAt(a)));

                        Paint mPaint = getPaint(fontsize, false, false);
                        totalWidth = mPaint.measureText(stringBuffer.toString());

                    }
                    if (ALIGN_CENTER == align) {
                        xPoint = (int) (maxLine - totalWidth) / 2;
                    } else if (ALIGN_RIGHT == align) {
                        if (totalWidth <= maxLine) {
                            xPoint = (int) (maxLine - totalWidth);
                        }
                    }
                    if (xPoint < 0) {
                        xPoint = 0;
                    }
                    if (font == FONT_LARGE) {
                        fontStyle |= 0x4000; // 大字体将字体进行拉高
                    }
                    String firstPrint = printText.substring(0, blodFrontSize);
                    if (isFrontBlod){
                        fontStyle |= 0x0001;
                    }else {
                        fontStyle &= 0xfffE;
                    }
                    int printHeight = mPrinter.drawTextEx(firstPrint, xPoint, currentYPoint, (int) maxLine,
                            -1, fontName, fontsize, 0, fontStyle, nowrap);

                    String secondPrint = printText.substring(blodFrontSize);
                    if (!TextUtils.isEmpty(secondPrint)) {
                        //计算第二次打印X轴的坐标（加上第一次打印占位字符的长度）
                        String distanceStr = stringBuffer.toString().substring(0, blodFrontSize);
                        xPoint += (mPaint.measureText(distanceStr) - getPointXForkRake(distanceStr,font));
                        if (isFrontBlod){
                            fontStyle &= 0xfffE;
                        }else {
                            fontStyle |= 0x0001;
                        }
                        mPrinter.drawTextEx(secondPrint, xPoint, currentYPoint, (int) maxLine,
                                -1, fontName, fontsize, 0, fontStyle, nowrap);
                    }
                    currentYPoint += printHeight;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public void feedLine(int lines){

        for (int i = 0; i < lines; i++) {
            addText(" ", 0, 0);
        }
    }

    public int appendImage(int xPoint, Bitmap bitmap){
        // TODO Auto-generated method stub
        int result = -1;
        if (mPrinter != null && bitmap != null) {
            if (!initPage) {
                mPrinter.setupPage(-1, -1);
                initPage = true;
            }
            int heigth = bitmap.getHeight();
            result = mPrinter.drawBitmap(bitmap, xPoint, currentYPoint);
            if (result == 0)
                currentYPoint += heigth;
        }
        return result;
    }

    /*public void addBarCode(String barcode, int align, int width, int height){

        int result = -1;
        if (mPrinter != null && barcode != null) {
            if (!initPage) {
                mPrinter.setupPage(-1, -1);
                initPage = true;
            }

            Bitmap bitmap = EncodingHandler.creatBarcode(mContext, barcode, 1, 100, false, 1, BarcodeFormat.CODE_128);
            int x = 3;
            Bitmap bitmap1 = Bitmap.createBitmap(bitmap, x, 0, bitmap.getWidth() - x, bitmap.getHeight(), null, false);
            Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap1, width + x + 5, height, true);
            x += 1;
            Bitmap bitmap2 = Bitmap.createBitmap(scaledBitmap, x, 0, scaledBitmap.getWidth() - x, scaledBitmap.getHeight(), null, false);
            //  mPrinter.drawBitmap(bitmap2, 0, currentYPoint + 10);
            //  currentYPoint += (120 + 40);
            appendBitmap(bitmap2, align);
            addText(barcode, 0, 1);
        }
    }*/

    /**
     * 加入打印位图，自动回车换行
     *
     * @param bitmap：位图信息
     * @param alignment：参考内容对齐定义
     * @return ret 0：成功
     * -1：失败
     */
    public int appendBitmap(Bitmap bitmap, int alignment) {
        int result = -1;
        // TODO 打印机厂商按此描述实现功能
        currentYPoint += 10;
        if (mPrinter != null && bitmap != null) {
            int xPoint = 0;
            int width = bitmap.getWidth();
            int heigth = bitmap.getHeight();
            if (alignment == ALIGN_LEFT) {
                result = mPrinter.drawBitmap(bitmap, xPoint, currentYPoint);
            } else if (alignment == ALIGN_RIGHT) {
                if (width >= MAX_PAGEWIDTH) {
                    xPoint = 0;
                } else {
                    xPoint = (MAX_PAGEWIDTH - width);
                }
                result = mPrinter.drawBitmap(bitmap, xPoint, currentYPoint);
            } else if (alignment == ALIGN_CENTER) {
                if (width >= MAX_PAGEWIDTH) {
                    xPoint = 0;
                } else {
                    xPoint = (MAX_PAGEWIDTH - width) / 2;
                }
                result = mPrinter.drawBitmap(bitmap, xPoint, currentYPoint);
            }
            if (result == 0)
                currentYPoint += heigth + 10;
            Log.d("koolPOS", " appendBitmap curHeigth= " + currentYPoint + "==xPoint= " + xPoint);
        }
        return result;
    }

    public void addImage(byte[] imageData, int offset, int width1, int height1){

        int width = 300;
        int height = 100;
        width = width1;
        height = height1;
        int size = (height * width);
        if (imageData != null && imageData.length > 0) {
            Bitmap image = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
            if (image != null) {
                int result = appendImage(offset, image);
                image.recycle();
                image = null;
            }
        } else {
            return;
        }
    }

    /*public void addQrCode(String qrCode, int offset, int height){
        // TODO Auto-generated method stub

        if(offset>55){
            offset-=55;
        }
        int calHeight = height - offset * 2;
        if (height > MAX_PAGEWIDTH) {
            height = MAX_PAGEWIDTH;
        }
        try {
            if (qrCode != null && qrCode.length() != 0) {
                Bitmap qrCodeBmp =
                        EncodingHandler.createQRImage(qrCode, height, height);
                if (qrCodeBmp != null) {
                    int result = appendImage(offset, qrCodeBmp);
                    qrCodeBmp.recycle();
                    qrCodeBmp = null;
                } else {
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }*/

    public void setGray(int grayLevel){
        // TODO Auto-generated method stub
        //0-10
        int tmp = (grayLevel / 2);
        if(grayLevel>8){
            tmp = 8;
        }
        mPrinter.setGrayLevel(tmp);
    }


    public int startPrint(){
        // TODO Auto-generated method stub

        if (mStatus == ERROR_BUSY) {
            return -1;
        }
        if (mPrinter != null && currentYPoint >= 0) {
            mStatus = ERROR_BUSY;
            if (feedLine == 0) {// 自动切纸补尝
                currentYPoint += mPrinter.drawTextEx("\n", 0, currentYPoint, MAX_PAGEWIDTH, -1,
                        fontName, DEF_FONT_SIZE, 0, 0, 0);
                currentYPoint += mPrinter.drawTextEx("\n", 0, currentYPoint, MAX_PAGEWIDTH, -1,
                        fontName, DEF_FONT_SIZE, 0, 0, 0);
                currentYPoint += mPrinter.drawTextEx("\n", 0, currentYPoint, MAX_PAGEWIDTH, -1,
                        fontName, DEF_FONT_SIZE, 0, 0, 0);
            } else {
                byte[] feed = new byte[feedLine];
                Arrays.fill(feed, (byte) 0x0A);
                String feedLines = new String(feed);
                currentYPoint += mPrinter.drawTextEx(feedLines, 0, currentYPoint, MAX_PAGEWIDTH, -1,
                        fontName, DEF_FONT_SIZE, 0, 0, 0);
            }

            int status = mPrinter.getStatus();
            Log.e("PrintManager", ""+status);

            int result = mPrinter.printPage(0);

            feedLine = 0;
            initPage = true;
            mPrinter.setupPage(-1, -1);
            mPrinter.clearPage();
            mPrinter.prn_clearPage();

            status = mPrinter.getStatus();
            while(status==-4){
                status = mPrinter.getStatus();
                if(status!=-4){
                    break;
                }
            }

            if(status !=0 ){
                return status;
            }else{
                return result;
            }
        } else {
            if (mStatus == ERROR_BUSY) {
                return mStatus;
            } else {
                return -1;
            }
        }
    }

    public int close() {
        if (mPrinter != null) {
            mPrinter.clearPage();
            mPrinter.close();
            mPrinter = null;
            mStatus = 0;
        }
        initPage = false;
        return 0;
    }

    public Rect getTextSize(String text, int fontSize) {
        Paint pFont = new Paint();
        pFont.setTextSize(fontSize);
        Rect rect = new Rect();
        pFont.getTextBounds(text, 0, text.length(), rect);
        return rect;
    }

    private Paint getPaint(int size, boolean bold, boolean fontItalic) {
        Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.reset();
        mPaint.setColor(Color.BLACK);
        mPaint.setTextSize(size);
        mPaint.setFakeBoldText(bold);
        if (fontItalic)
            mPaint.setTextSkewX(-0.5f);
        return mPaint;
    }

    //将字符转化为同类型的占位字符
    private char getCharSeat(char originalChar){
        char newChar = originalChar;
        if (Character.isSpaceChar(newChar)){
            newChar = '=';
        }else if (newChar >= 'a' && newChar <= 'z'){
            newChar = 'a';
        }else if (newChar >= 'A' && newChar <= 'Z'){
            newChar = 'A';
        }else if (newChar >= '0' && newChar <= '9'){
            newChar = '0';
        }else if (newChar == '*'){
            newChar = '*';
        }
        return newChar;
    }

    /**
     * 默写字符在计算的长度会大于实际的长度，该方法为计算需要减去的X轴坐标
     * @return float X坐标需要前移的距离
     */
    private float getPointXForkRake(String text,int font){
        float distance = 0;
        if (TextUtils.isEmpty(text)){
            return distance;
        }
        for (int a = 0; a < text.length(); a++) {
            if (Character.isSpaceChar(text.charAt(a))
                    || (text.charAt(a) >= 'a' && text.charAt(a) <= 'z' || text.charAt(a) >= 'A' && text.charAt(a) <= 'Z')
                    || (text.charAt(a) >= '0' && text.charAt(a) <= '9')
                    || text.charAt(a) == '*') {
                if (font == FONT_NORMAL) {
                    distance += 1.1;
                } else if (font == FONT_LARGE) {
                    distance += 1.1;
                } else {
                    distance += 0.9;
                }
            }
        }
        return distance;
    }


}
