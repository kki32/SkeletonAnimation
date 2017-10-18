//  ========================================================================
//  COSC422: Advanced Computer Graphics;  University of Canterbury (2017)
//
//  FILE NAME: ModelLoader.cpp
//
//  This is a modified version of the sample program included with the Assimp library
//    from http://www.assimp.org/main_downloads.html
//
//	Includes only basic functions (no texture mapping or skeletal animations)
//  Press key '1' to toggle 90 degs model rotation about x-axis on/off.
//  See Ex10.pdf for details.
//  ========================================================================

#include <iostream>
#include <fstream>
#include <GL/freeglut.h>

using namespace std;

#include <assimp/cimport.h>
#include <assimp/types.h>
#include <assimp/scene.h>
#include <vector>
#include <assimp/postprocess.h>
#include "assimp_extras.h"

const aiScene* scene = NULL;
GLuint scene_list = 0;
float angle = 0;
float pos = 0;
aiVector3D scene_min, scene_max, scene_center;
bool modelRotn = true;
ofstream fileout;
int animationStep = 0;
int tick = 0;
int tick2 = 0;
aiVector3D* verts;
aiVector3D* norm;
//int[] norm;
int updateTime = 5;
//float TicksPerSec = 25; //4sec

bool loadModel(const char* fileName)
{
	scene = aiImportFile(fileName, aiProcessPreset_TargetRealtime_Quality);
	if(scene == NULL) exit(1);
	printSceneInfo(fileout, scene);
	printTreeInfo(fileout, scene->mRootNode);
	printAnimInfo(fileout, scene);
	get_bounding_box(scene, &scene_min, &scene_max);
	return true;
}

// ------A recursive function to traverse scene graph and render each mesh----------
void render (const aiScene* sc, const aiNode* nd)
{

	aiMatrix4x4 m = nd->mTransformation;
	aiTransposeMatrix4(&m);   //Convert to column-major order
	glPushMatrix();
	glMultMatrixf((float*)&m);   //Multiply by the transformation matrix for this node

	aiMesh* mesh;
	aiFace* face;
/*
	int index = 0;
	int vertSize = 0;
	
	for (uint n = 0; n < nd->mNumMeshes; n++)
	{
		mesh = scene->mMeshes[nd->mMeshes[n]];

		for (uint iv = 0; iv < mesh->mNumVertices; iv++)
		{
			vertSize++;
		}
	}
	* verts = new aiVector3D[vertSize];
	*/ 
	
		

	// Draw all meshes assigned to this node
	for (uint n = 0; n < nd->mNumMeshes; n++)
	{
		mesh = scene->mMeshes[nd->mMeshes[n]];

/*
		for (uint iv = 0; iv < mesh->mNumVertices; iv++)
		{
			verts[index] = mesh->mVertices[iv];
			norm[index] =  mesh->mNormals[iv];
			index++;
		}
		
		cout << index;
		*/ 
	
		apply_material(sc->mMaterials[mesh->mMaterialIndex]);

		if(mesh->HasNormals())
			glEnable(GL_LIGHTING);
		else
			glDisable(GL_LIGHTING);


		if(mesh->HasVertexColors(0))
			glEnable(GL_COLOR_MATERIAL);
		else
			glDisable(GL_COLOR_MATERIAL);

		//Get the polygons from each mesh and draw them
		for (uint k = 0; k < mesh->mNumFaces; k++)
		{
			face = &mesh->mFaces[k];
			GLenum face_mode;

			switch(face->mNumIndices)
			{
				case 1: face_mode = GL_POINTS; break;
				case 2: face_mode = GL_LINES; break;
				case 3: face_mode = GL_TRIANGLES; break;
				default: face_mode = GL_POLYGON; break;
			}

			glBegin(face_mode);

			for(uint i = 0; i < face->mNumIndices; i++) {
				int index = face->mIndices[i];
				if(mesh->HasVertexColors(0))
				{
					glEnable(GL_COLOR_MATERIAL);
					glColorMaterial(GL_FRONT_AND_BACK, GL_DIFFUSE);
					glColor4fv((GLfloat*)&mesh->mColors[0][index]);
				}
				else
					glDisable(GL_COLOR_MATERIAL);
				if(mesh->HasNormals())
					glNormal3fv(&mesh->mNormals[index].x);
				glVertex3fv(&mesh->mVertices[index].x);
			}

			glEnd();
		}

	}


			
	// Draw all children
	for (uint i = 0; i < nd->mNumChildren; i++)
		render(sc, nd->mChildren[i]);


	glPopMatrix();
}

//--------------------OpenGL initialization------------------------
void initialise()
{
    glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
	glEnable(GL_LIGHTING);
	glEnable(GL_LIGHT0);
	glEnable(GL_DEPTH_TEST);
	glEnable(GL_NORMALIZE);
	glLightModeli(GL_LIGHT_MODEL_TWO_SIDE, GL_TRUE);
	glColorMaterial(GL_FRONT_AND_BACK, GL_DIFFUSE);
	fileout.open("BVH_Files/sceneInfo.txt", ios::out);
	//loadModel("BVH_Files/Dance.bvh");
	loadModel("Model Files/dwarf.x");		//<<<-------------Specify input file name here  --------------
	//loadModel("Models/wuson.x");		//<<<-------------Specify input file name here  --------------
	
	glMatrixMode(GL_PROJECTION);
	glLoadIdentity();
	gluPerspective(45, 1, 1.0, 1000.0);
}

//----Timer callback for continuous rotation of the model about y-axis----
void update(int value)
{

	aiMatrix4x4 matPos;
	aiMatrix4x4 matRotn3;
	aiNodeAnim *chnl;
	aiVector3D posn;
	aiQuaternion rotn;
	aiAnimation *anim;
	aiMatrix4x4 matprod;
	aiMatrix4x4 Bprod;

	aiMatrix4x4 m;

		for (uint j = 0; j < scene->mNumAnimations; j++){
		anim = scene->mAnimations[j];
		
		if(animationStep > (int) anim->mDuration){
			animationStep = 0;
		} else{
			//animationStep += 1;
		}
	
		//get motion data and replace matrix with it
		for (uint i = 0; i < anim->mNumChannels; i++){
		tick = animationStep;
		//cout << anim->mTicksPerSec << endl;
		//tick2 = animationStep;
		chnl = anim->mChannels[i];
		//double t = glutGet(GLUT_ELAPSED_TIME);
		//tick = (int) (time/1000);
		//if(tick <= (int) chnl->mNumPositionKeys){
			
			if(chnl->mNumPositionKeys == 1){
				posn = chnl->mPositionKeys[0].mValue;
			} else {
				posn = chnl->mPositionKeys[tick].mValue;
			}
		

			if(chnl->mNumRotationKeys == 1){
				rotn = chnl->mRotationKeys[0].mValue;
		
			} else {
			
				rotn = chnl->mRotationKeys[tick].mValue;
				if(i > 0 && (chnl->mRotationKeys[i-1].mTime < tick2 && tick2 <= chnl->mRotationKeys[i].mTime)){
					aiQuaternion rotn1 = (chnl->mRotationKeys[i-1]).mValue;
					aiQuaternion rotn2 = (chnl->mRotationKeys[i]).mValue;
					double time1 = (chnl->mRotationKeys[i-1]).mTime;
					double time2 = (chnl->mRotationKeys[i]).mTime;
					double factor = (tick-time1)/(time2-time1);
					rotn.Interpolate(rotn, rotn1, rotn2, factor);
				} 
			}

			matPos.Translation(posn, matPos);
			aiMatrix3x3 matRotn3 = rotn.GetMatrix();
			aiMatrix4x4 matRot = aiMatrix4x4(matRotn3);
			matprod = matPos * matRot;
			
			aiNode* node = scene->mRootNode->FindNode(chnl->mNodeName);
			node->mTransformation = matprod;
		
		
			for (uint n = 0; n < scene->mNumMeshes; n++)
			{
				aiMesh* mesh = scene->mMeshes[n];
			
				for (uint bi = 0; bi < mesh->mNumBones; bi++)
				{
					aiBone* bone = mesh->mBones[bi];
					Bprod = bone->mOffsetMatrix;

					aiNode* currentNode = scene->mRootNode->FindNode(bone->mName); //Q0
		
					while(currentNode != scene->mRootNode){ //Q1-QR	 //
						Bprod = Bprod * currentNode->mTransformation ;
						currentNode = currentNode->mParent;		
					}
						
					//when mRootNode
					Bprod =  Bprod * currentNode->mTransformation;
					
					int off = mesh->mNumVertices;
					int vid = (bone->mWeights[0]).mVertexId;
					cout << vid << "   ";
					aiMatrix4x4 D = Bprod.Inverse().Transpose(); //TODO not sure whether correct interpret?
				
					mesh->mVertices[vid] = Bprod * verts[vid+off];
					mesh->mNormals[vid] = D * norm[vid+off]; //TODO have +off
				}
			
			}
			
		
		}
	}
	
		
		
	
			
	render(scene, scene->mRootNode);
	glutPostRedisplay();
	glutTimerFunc(updateTime, update, 0);
	
	

}

//----Keyboard callback to toggle initial model orientation---
void keyboard(unsigned char key, int x, int y)
{
	if(key == '1') modelRotn = !modelRotn;   //Enable/disable initial model rotation
	if(key == '2') animationStep += 1; 
	glutPostRedisplay();
}

//------The main display function---------
//----The model is first drawn using a display list so that all GL commands are
//    stored for subsequent display updates.
void display()
{

	float pos[4] = {50, 50, 50, 1};
	glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

	glMatrixMode(GL_MODELVIEW);
	glLoadIdentity();
	gluLookAt(0, 0, 3, 0, 0, -5, 0, 1, 0);
	glLightfv(GL_LIGHT0, GL_POSITION, pos);

	glRotatef(angle, 0.f, 1.f ,0.f);  //Continuous rotation about the y-axis
	if(modelRotn) glRotatef(-90, 1, 0, 0);		  //First, rotate the model about x-axis if needed.

	// scale the whole asset to fit into our view frustum
	float tmp = scene_max.x - scene_min.x;
	tmp = aisgl_max(scene_max.y - scene_min.y,tmp);
	tmp = aisgl_max(scene_max.z - scene_min.z,tmp);
	tmp = 1.f / tmp;
	glScalef(tmp, tmp, tmp);

	aiNode* rootNode = scene->mRootNode;

    // center the model
	//glTranslatef( -scene_center.x, -scene_center.y, -scene_center.z );

        // if the display list has not been made yet, create a new one and
        // fill it with scene contents
	/*if(scene_list == 0)
	{
	* 
	    scene_list = glGenLists(1);
	    glNewList(scene_list, GL_COMPILE);
            // now begin at the root node of the imported data and traverse
            // the scenegraph by multiplying subsequent local transforms
            // together on GL's matrix stack.
            * */
	    
	       
	       
	aiMesh* mesh;
	int index = 0;
	int vertSize = 0;
	
	for (uint n = 0; n < scene->mNumMeshes; n++)
	{
		for (uint iv = 0; iv < scene->mMeshes[n]->mNumVertices; iv++)
		{
			vertSize++;
		}
	}
	

	verts = new aiVector3D[vertSize];
	norm = new aiVector3D[vertSize];

	for (uint n = 0; n < scene->mNumMeshes; n++)
	{
	
		mesh = scene->mMeshes[n];

		for (uint iv = 0; iv < mesh->mNumVertices; iv++)
		{
			verts[index] = mesh->mVertices[iv];
			norm[index] =  mesh->mNormals[iv];

			index++;
		}
		
	}

	       /*
	    glEndList();
	}

	glCallList(scene_list);
	*/
	
	//create floor
/*
	glBegin(GL_QUADS);
	double planeY = 2;
	double leftX = -50;
	double rightX = 50;
	double leftZ = 50;
	double rightZ = -50;
	glVertex3f( leftX,planeY, leftZ);
	glVertex3f( leftX,planeY,rightZ);
	glVertex3f(rightX,planeY,rightZ);
	glVertex3f(rightX,planeY, leftZ);
	glEnd();
	*/
	//
	   render(scene, rootNode);
	glutSwapBuffers();
}



int main(int argc, char** argv)
{
	glutInit(&argc, argv);
	glutInitDisplayMode(GLUT_RGB | GLUT_DOUBLE | GLUT_DEPTH);
	glutInitWindowSize(600, 600);
	glutCreateWindow("Assimp Test");
	glutInitContextVersion (4, 2);
	glutInitContextProfile ( GLUT_CORE_PROFILE );

	initialise();
	glutDisplayFunc(display);
	glutTimerFunc(updateTime, update, 0);
	glutKeyboardFunc(keyboard);
	glutMainLoop();

	aiReleaseImport(scene);
}

