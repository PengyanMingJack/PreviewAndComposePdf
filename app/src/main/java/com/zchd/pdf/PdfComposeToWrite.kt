package com.zchd.pdf

import com.itextpdf.io.font.PdfEncodings
import com.itextpdf.io.font.constants.StandardFonts
import com.itextpdf.io.image.ImageDataFactory
import com.itextpdf.kernel.colors.ColorConstants
import com.itextpdf.kernel.colors.DeviceRgb
import com.itextpdf.kernel.font.PdfFontFactory
import com.itextpdf.kernel.geom.PageSize
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Cell
import com.itextpdf.layout.element.Image
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.element.Table
import com.itextpdf.layout.properties.TextAlignment
import com.itextpdf.layout.properties.UnitValue
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.util.*

class PdfComposeToWrite {

    private val fontStyle = Utils.getAssetsCacheFile(Utils.getApplication(), "SimSun.ttf")
    private val font = PdfFontFactory.createFont(
        fontStyle, PdfEncodings.IDENTITY_H, PdfFontFactory.EmbeddingStrategy.FORCE_EMBEDDED
    )

    companion object {
        var isSignPdf: Boolean = true
        val defPdfFilePath get() = Utils.getApplication().cacheDir.path + "/def_authorize_book.pdf"
        val signPdfFilePath get() = Utils.getApplication().cacheDir.path + "/sign_authorize_book.pdf"
    }

    private var pdfDocument: Document? = null
    private var mSignInfo: SignPdfInfo? = null
    private fun drawEmptyA4() {
        try {
            var pdfWriter =
                PdfWriter(FileOutputStream(if (isSignPdf) signPdfFilePath else defPdfFilePath))
            pdfDocument = Document(PdfDocument(pdfWriter), PageSize.A4, false).apply {
                setFont(PdfFontFactory.createFont(StandardFonts.COURIER))
                setFontSize(14f)
                setFontColor(DeviceRgb(51, 51, 51))
            }
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun buildAuthorizeBook(signInfo: SignPdfInfo? = null) {
        pdfDocument?.close()
        pdfDocument = null
        isSignPdf = signInfo != null
        drawEmptyA4()
        mSignInfo = signInfo
        pdfDocument?.add(Paragraph(title).apply {
            setTextAlignment(TextAlignment.CENTER)
            setFont(font)
            setFontColor(ColorConstants.BLACK)
            setFontSize(22f)
            setMarginTop(20f)
        })
        content.forEach {
            pdfDocument?.add(Paragraph(it).apply {
                setFont(font)
                setFirstLineIndent(44f)
            })
        }
        pdfDocument?.add(Paragraph(
            "本人声明：本人已仔细阅读上述所有条款及填写须知，且对所有条" + "款的含义及相应的法律后果已全部知晓并充分理解，本人自愿作出上述" + "授权、承诺和声明。"
        ).apply {
            setFont(font)
            setBold()
            setFontColor(ColorConstants.BLACK)
            setFirstLineIndent(44f)
        })
        createTable1()
        createTable2()
        pdfDocument?.add(Paragraph(
            "经办人员:"
        ).apply {
            setFont(font)
            setFontSize(12f)
        })
        pdfDocument?.add(Paragraph(
            DateUtil.convertToString(Date().time, DateUtil.FORMAT_YYYYCMMCDD)
        ).apply {
            setFont(font)
            setFontSize(10f)
            setMarginTop(10f)
            setTextAlignment(TextAlignment.RIGHT)
        })
        pdfDocument?.add(Paragraph(
            "填写须知："
        ).apply {
            setFont(font)
            setBold()
            setMarginTop(40f)
            setFontColor(ColorConstants.BLACK)
        })
        pdfDocument?.add(Paragraph(
            remark
        ).apply {
            setFont(font)
            setMarginTop(10f)
        })
        try {
            pdfDocument?.close()
        } catch (e: Exception) {
        }
    }

    private fun createTable1() {
        pdfDocument?.add(Paragraph("授权人信息").apply {
            setTextAlignment(TextAlignment.CENTER)
            setFont(font)
            setMarginTop(40f)
        })
        val unitValue = arrayOf(
            UnitValue.createPercentValue(10f),
            UnitValue.createPercentValue(15f),
            UnitValue.createPercentValue(22f),
            UnitValue.createPercentValue(15f),
            UnitValue.createPercentValue(15f),
            UnitValue.createPercentValue(20f)
        )
        val table = Table(unitValue).apply {
            width = UnitValue.createPercentValue(100f)
            setFont(font)
            setFixedLayout()
            setFontSize(12f)
            setTextAlignment(TextAlignment.CENTER)
        }
        table.addHeaderCell("姓名")
        table.addHeaderCell("证件类型")
        table.addHeaderCell("证件号")
        table.addHeaderCell("与户主关系")
        table.addHeaderCell("签名/指印")
        table.addHeaderCell("委托代理人/法\n定代理人姓名")
        table.addFooterCell(Cell().apply {
            setHeight(20f)
            add(Paragraph(mSignInfo?.userName ?: ""))
        })
        table.addFooterCell(mSignInfo?.userCardType ?: "")
        table.addFooterCell(mSignInfo?.userIdCard ?: "")
        table.addFooterCell(mSignInfo?.houseRelation ?: "")
        val tableWidth = table.width
        table.addFooterCell(Cell().apply {
            if (mSignInfo?.fingerPicPath.isNullOrEmpty() && mSignInfo?.signPicPath.isNullOrEmpty()) {
                if (null != mSignInfo) {
                    add(Paragraph("/"))
                }
            } else {
                if (mSignInfo?.signPicPath?.isNotBlank() == true) {
                    add(Image(ImageDataFactory.create(mSignInfo?.signPicPath)).apply {
                        width = UnitValue.createPercentValue(80f)
                    })

                }
                if (mSignInfo?.fingerPicPath?.isNotBlank() == true) {
                    add(Image(ImageDataFactory.create(mSignInfo?.fingerPicPath)).apply {
                        width = UnitValue.createPercentValue(80f)
//                        setFixedPosition(1,350f,230f,80f)
                    })

                }

            }
        })
//        table.addFooterCell(Image(ImageDataFactory.create(mSignInfo?.signPicPath)).apply {
//            width = UnitValue.createPercentValue(80f)
//        })
        table.addFooterCell(mSignInfo?.repName ?: "")

        pdfDocument?.add(table)
    }

    private fun createTable2() {
        pdfDocument?.add(Paragraph("委托代理人/法定代理人信息").apply {
            setTextAlignment(TextAlignment.CENTER)
            setFont(font)
            setMarginTop(40f)
        })
        val unitValue = arrayOf(
            UnitValue.createPercentValue(10f),
            UnitValue.createPercentValue(15f),
            UnitValue.createPercentValue(22f),
            UnitValue.createPercentValue(15f),
            UnitValue.createPercentValue(15f),
        )
        val table = Table(unitValue).apply {
            width = UnitValue.createPercentValue(100f)
            setFont(font)
            setFixedLayout()
            setFontSize(12f)
            setTextAlignment(TextAlignment.CENTER)
        }
        table.addHeaderCell("姓名")
        table.addHeaderCell("证件类型")
        table.addHeaderCell("证件号")
        table.addHeaderCell("与授权人关系")
        table.addHeaderCell("签名/指印")
        table.addFooterCell(Cell().apply {
            setHeight(20f)
            add(Paragraph(mSignInfo?.repName ?: ""))
        })
        table.addFooterCell(mSignInfo?.repCardType ?: "")
        table.addFooterCell(mSignInfo?.repIdCard ?: "")
        table.addFooterCell(mSignInfo?.authRelation ?: "").apply {}
        table.addFooterCell(Cell().apply {
            if (mSignInfo?.repSignPicPath.isNullOrEmpty() && mSignInfo?.repSignPicPath.isNullOrEmpty()) {
                if (null != mSignInfo) {
                    add(Paragraph("/"))
                }
            } else {
                if (mSignInfo?.repSignPicPath?.isNotBlank() == true) {
                    add(Image(ImageDataFactory.create(mSignInfo?.repSignPicPath)).apply {
                        width = UnitValue.createPercentValue(80f)
                    })
                }
                if (mSignInfo?.repFingerPicPath?.isNotBlank() == true) {
                    add(Image(ImageDataFactory.create(mSignInfo?.repFingerPicPath)).apply {
                        width = UnitValue.createPercentValue(80f)
                    })
                }

            }

        })
        pdfDocument?.add(table)
    }

    var title = "居民家庭经济状况核对授权书"
    var content = arrayListOf<String>(
        "本人同意授权审批机构及全国各级居民家庭经济状况核对机构通 过政府机构、金融机构、提供货币资金转移服务的非银行支付机构、 大数据管理及服务机构、公共事业单位、相关行业性组织和社会团体等 涉及本人基本信息及家庭经济状况信息的机构、单位、部门，就社会救 助、社会保障、社会福利以及其他需要依据居民家庭经济状况进行行政 确认、行政给付、行政审批的相关事项，对本人基本信息及家庭经济状况信息进行查询、 核对 。",
        "本人亦同意授权合法留存本人基本信息和家庭经济状况信息的前述机构予以配合提供。",
        "本授权有效期限自签署之日起至申请人 3退出该行政事项止，含申请人纳入监测范围期间。",
        "本人承诺以下身份证件号码、签名（或指印）均真实有效，如有虚构、隐瞒、伪造，本人愿意承担相应法律责任及后果。"
    )
    var remark =
        "1、审批机构 1包括但不限于乡镇（街道）及以上人民政府、县级及以上社会救助主管部门、乡村振兴部门。2、政府机构 2包括但不限于：财政、公安、卫健、医疗、人社、自然资源、市场监管、统计、残联、教育、 农业、退役军人、计生、民政、法院、司法、扶贫、工会、应急管理、通信、能源、征信。3、申请人 3指授权人本人以及与本人相关的其他申请人。4、授权人为无民事行为能力人、限制民事行为能力人的，由其法定代理人签署，并在委托代理人/法定代理人信息表中填写相关信息。5、采用纸质授权书方式授权的，应由授权人本人或代理人亲笔签名或按捺指印以确认；采用电子授权书方式授权的，需经信息比对以确认授权人本人或代理人身份后，通过电子签名方式确认授权。6、委托代理人应确保其于签署本授权书时已取得授权人本人的有效授权，并就授权的真实性和合法性承担相应法律责任"

}