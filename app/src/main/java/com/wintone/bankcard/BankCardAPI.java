package com.wintone.bankcard;

public class BankCardAPI {
    public native String GetBankInfo(String str);

    public native String GetKernalVersion();

    public native int RecognizeNV21(byte[] bArr, int i, int i2, int[] iArr, char[] cArr, int i3, int[] iArr2, int[] iArr3);

    public native int RecognizeStreamNV21Ex(byte[] bArr, int i, int i2, int[] iArr, char[] cArr, int i3, int[] iArr2);

    public native void WTDetectFrameLines(byte[] bArr, int i, int i2, int i3, int[] iArr);

    public native void WTGetCharPos(int i, int[] iArr);

    public native int WTInitCardKernal(String str, int i);

    public native int WTRecognizeImage(String str, char[] cArr, int i, int[] iArr);

    public native int WTRecognizeMemory(int[] iArr, int i, int i2, int i3, char[] cArr, int i4, int[] iArr2);

    public native int WTRecognizeStreamNV21(byte[] bArr, int i, int i2, char[] cArr, int i3, int[] iArr);

    public native void WTSetROI(int[] iArr, int i, int i2);

    public native void WTUnInitCardKernal();

    static {
        System.loadLibrary("AndroidBankCard");
    }
}