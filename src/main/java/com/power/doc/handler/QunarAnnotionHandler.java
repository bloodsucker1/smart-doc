package com.power.doc.handler;

import com.power.common.util.StringUtil;
import com.power.doc.model.qunar.ServiceDocModel;
import com.thoughtworks.qdox.model.JavaAnnotation;

/**
 * QunarAnnotationHandler
 *
 * @author wangyafei.wang
 * @create 2020-07-28 17:11
 **/
public class QunarAnnotionHandler {

    public ServiceDocModel serviceDocHandler(JavaAnnotation annotation) {
        ServiceDocModel serviceDocModel = new ServiceDocModel();
        serviceDocModel.setDefine(StringUtil.trimBlank(String.valueOf(annotation.getNamedParameter("define"))));
        serviceDocModel.setDesc(StringUtil.trimBlank(String.valueOf(annotation.getNamedParameter("desc"))));
        serviceDocModel.setScene(StringUtil.trimBlank(String.valueOf(annotation.getNamedParameter("scene"))));
        serviceDocModel.setNotice(StringUtil.trimBlank(String.valueOf(annotation.getNamedParameter("notice"))));
        return serviceDocModel;
    }
}
