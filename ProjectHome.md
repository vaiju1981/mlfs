所有代码使用GPL协议授权，这意味着你可以自由使用、修改、再发布代码，但是使用这里代码的程序必须是开源的，而且遵守GPL协议。
了解更多：http://www.gnu.org/licenses

所有其他内容通过知识共享协议CC-by共享，http://creativecommons.org/licenses/by/3.0
语料资源我以注明出处，请遵守语料提供者的协议。

项目名Machine Learning From Scratch(MLFS)的灵感来源于[LFS](http://www.linuxfromscratch.org/)


此项目的目的是通过一些应用场景，实现一些统计机器学习模型，注意目的是对模型的理解和熟悉，并不追求在某应用场景下的指标，因为否则就关注点就变成了如何使用而不是实现模型


<font color='red'>本人水平有限，而且项目也处在初始阶段，还望多多指教，关于本项目的任何问题都可以给我Email：</font><br>
<img src='http://luoleicn-wordpress.stor.sinaapp.com/uploads/2011/08/mail.gif' />

<h3>已经实现以下内容:</h3>


<b>子项目1：隐马尔科夫模型</b>

应用场景：中文词性标注<br>
<br>
实现方法：隐马模型，维特比解码，用线性插值算法平滑转移矩阵，用后缀树计算未登录词的发射概率。<br>
<br>
语料：<a href='http://code.google.com/p/mlfs/downloads/detail?name=pos.tgz&can=2&q=#makechanges'>conll06中文语料</a>

准确率：登录词0.8937187843900787，未登录词0.5285295076622106，总体0.87146008465987<br>
<br>
改进建议：Beam Search加速维特比解码<br>
<br><br>
<b>子项目2：最大熵模型</b>

应用场景：文本分类<br>
<br>
实现方法：参数估计GIS、L-BFGS，对模型参数采用高斯平滑，对未出现的词和类别出现次数使用0.1平滑。<br>
<br>
语料：百度知道问题分类<br>
<br>
准确率：未用高斯平滑66.94%，高斯平滑69.39%<br>
<br>
对照实验<a href='http://homepages.inf.ed.ac.uk/lzhang10/maxent_toolkit.html'>zhangle's最大熵</a>未用高斯平滑66.59%，高斯平滑69.15%<br>
<br>
对照实验<a href='http://www.csie.ntu.edu.tw/~cjlin/liblinear/'>liblinear</a>68.80%<br>
<br>
改进建议：增加基于最大熵的feature selection算法<br>
<br><br>
<b>子项目3：voted perceptron</b>

应用场景：二元分类<br>
<br>
语料：<a href='http://code.google.com/p/mlfs/downloads/detail?name=vp.tgz&can=2&q=#makechanges'>分类语料</a>

准确率：准确率83.81%，对照试验<a href='http://www.csie.ntu.edu.tw/~cjlin/liblinear/'>liblinear</a>准确率83.86%<br>
<br>
改进建议：增加Kernel算法<br>
<br><br>
<b>子项目4：条件随机场</b>

应用场景：中文分词<br>
<br>
参数估计：L-BFGS，高斯平滑<br>
<br>
语料：CTB5.0<br>
<br>
F值：97.13%<br>
<br>
改进建议：多线程加速参数估计<br>
<br><br>
<b>子项目5：Naive Bayes</b>

实现方法：Gibbs Sampling<br>
<br>
应用场景：文本分类<br>
<br>
准确率：准确率84.1%<br>
<br>
地址：因为其他子项目是Java实现，这个子项目是C++实现，所以放在了<a href='http://code.google.com/p/naive-bayes-gibbs-sampling/'>这里</a>
<br><br>
<b>子项目6：Restricted Boltzmann Machines</b>

实现方法：Contrastive Divergence<br>
<br>
<br><br>
<b>子项目7：SVD for Recsys</b>

实现方法：LBFGS, L2, R = PQ'<br>
<br>
应用场景：电影打分预测<br>
<br>
数据集：movielens-100k<br>
<br>
RMSE : 0.9489362093159761<br>
<hr />
<h3>to do list</h3>
SVM<br>
<br><br>
Deep Learning neural network<br>
<br><br>
LDA