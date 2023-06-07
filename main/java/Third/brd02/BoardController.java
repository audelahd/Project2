package Third.brd02;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FileUtils;

@WebServlet("/board/*")
public class BoardController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static String ARTICLE_IMAGE_REPO = "C:\\board\\article_image";
	BoardService boardService;
	ArticleVO articleVO;

	public BoardController() {

	}

	public void init(ServletConfig config) throws ServletException {
		boardService = new BoardService();
		articleVO = new ArticleVO();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doHandle(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doHandle(request, response);
	}

	protected void doHandle(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String nextPage="";
		request.setCharacterEncoding("utf-8");
		response.setContentType("text/html;charset=utf-8");
		String action = request.getPathInfo();
		System.out.println("action:"+action);
		
		try {
			List<ArticleVO> articlesList = new ArrayList<ArticleVO>();
			if(action ==null) {
				articlesList = boardService.listArticles();
				request.setAttribute("articlesList", articlesList);
				nextPage = "/board02/listArticles.jsp";
			}
			else if(action.equals("/listArticles.do")) {
				articlesList =boardService.listArticles();
				request.setAttribute("articlesList", articlesList);
				nextPage="/board02/listArticles.jsp";
			}
			else if(action.equals("/articleForm.do")) {
				nextPage="/board02/articleForm.jsp";
			}
			else if (action.equals("/addArticle.do")) {
				int articleNO=0;
				Map<String, String> articleMap = upload(request,response);
				String title = articleMap.get("title");
				String content = articleMap.get("content");
				String imageFileName = articleMap.get("imageFileName");
				
				articleVO.setParentNO(0);
				articleVO.setId("hong");
				articleVO.setTitle(title);
				articleVO.setContent(content);
				articleVO.setImageFileName(imageFileName);
				System.out.println(imageFileName);
				articleNO = boardService.addArticle(articleVO);	
				
				if (imageFileName != null && imageFileName.length() !=0) {
					System.out.println("들어왔나");
					File srcFile = new File(ARTICLE_IMAGE_REPO+"\\"+"temp"+"\\"+imageFileName);
					File destDir = new File(ARTICLE_IMAGE_REPO+"\\"+articleNO);
					System.out.println("디렉토리만들기");
					destDir.mkdirs();
					FileUtils.moveFileToDirectory(srcFile, destDir, true);
					System.out.println("디렉토리에 옮기기");
				}
				PrintWriter pw = response.getWriter();
				pw.print("<script>"+" alert('새 글을 추가했습니다.');"+" location.href='"
				+request.getContextPath()+"/board/listArticles.do';" +"</script>");
				return;
			}else {
					nextPage="/board02/listArticles.jsp";
				
				
			}
			RequestDispatcher dispatch = request.getRequestDispatcher(nextPage);
			dispatch.forward(request, response);
		}catch(Exception e) {
			e.printStackTrace();
		}
	}

	private Map<String, String> upload(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException{
	
		Map<String, String> articleMap = new HashMap<String, String>();
		String encoding="utf-8";
		File currentDirPath = new File(ARTICLE_IMAGE_REPO); //파일 객체 생성
		DiskFileItemFactory factory = new DiskFileItemFactory();
		factory.setRepository(currentDirPath);
		factory.setSizeThreshold(1024*1024);
		ServletFileUpload upload = new ServletFileUpload(factory);
		try {
			List items = upload.parseRequest(request);
			for (int i=0; i<items.size(); i++) {
				FileItem fileItem = (FileItem) items.get(i);
				if(fileItem.isFormField()) {
					System.out.println(fileItem.getFieldName() +"="+fileItem.getString(encoding));
					articleMap.put(fileItem.getFieldName(), fileItem.getString(encoding));
					//파일 업로드로 같이 전송된 새 글 관련 매개변수를 Map에 저장&반환 
				}
				else {
					System.out.println("파라미터이름: "+fileItem.getFieldName());
					System.out.println("파일 이믈:"+fileItem.getName());
					System.out.println("파일 크기:"+fileItem.getSize()+"bytes");
					
					if(fileItem.getSize()>0) { //파일 크기가 0보다 크면 (크기가 잡히면 존재한다는 뜻)
						int idx = fileItem.getName().lastIndexOf("\\");
						if(idx==-1) {
							idx=fileItem.getName().lastIndexOf("/");
						}
						String fileName = fileItem.getName().substring(idx+1);
						articleMap.put(fileItem.getFieldName(), fileName);
						File uploadFile = new File(currentDirPath+"\\temp\\"+fileName);
						fileItem.write(uploadFile);
					}
					
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return articleMap;
	}

}
